package com.redislabs.edu.redi2read.repositories

import com.redislabs.edu.redi2read.models.Cart
import com.redislabs.modules.rejson.JReJSON
import org.checkerframework.checker.guieffect.qual.SafeEffect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import java.util.stream.StreamSupport

@Repository
open class CartRepository : CrudRepository<Cart, String?> {
    private var redisJson = JReJSON()

    @Autowired
    private var template: RedisTemplate<String, String>? = null
    private fun redisSets(): SetOperations<String, String> {
        return template!!.opsForSet()
    }

    private fun redisHash(): HashOperations<String, String, String> {
        return template!!.opsForHash()
    }

    override fun <S : Cart?> save(cart: S): S {
        // set cart id
        if (cart?.id == null) {
            cart?.id = UUID.randomUUID().toString()
        }
        var key: S = cart
        redisJson[key.toString()] = cart
        redisSets().add(idPrefix, key.toString())
        if (cart != null) {
            redisHash().put("carts-by-user-id-idx", cart.userId, cart.id)
        }
        return cart
    }

    override fun <S : Cart?> saveAll(carts: Iterable<S>): Iterable<S> {
        return StreamSupport //
            .stream(carts.spliterator(), false) //
            .map { cart: S -> save(cart) } //
            .collect(Collectors.toList())
    }

    override fun findById(id: String?): Optional<Cart> {
        var cart : Cart = redisJson.get(/* key = */ getKey(id), Cart::class.java)
        return Optional.of(cart)

    }

    override fun existsById(id: String?): Boolean {
        return template!!.hasKey(getKey(id))
    }

    override fun findAll(): Iterable<Cart?> {
        var keys = redisSets().members(idPrefix).toTypedArray()
        return redisJson.mget(Cart::class.java, *keys)
    }

    override fun findAllById(ids: Iterable<String?>): Iterable<Cart?> {
        var keys = StreamSupport.stream(ids.spliterator(), false) //
            .map { id: String? -> getKey(id) }.collect(Collectors.toList()).toTypedArray()
        return redisJson.mget(Cart::class.java, *keys)
    }

    override fun count(): Long {
        return redisSets().size(idPrefix)
    }

    override fun deleteById(id: String?) {
        redisJson.del(getKey(id))
    }

    override fun delete(cart: Cart?) {
        deleteById(cart?.id)
    }

    override fun deleteAllById(strings: Iterable<String?>) {}
    override fun deleteAll(carts: Iterable<Cart?>) {
        var keys = StreamSupport //
            .stream(carts.spliterator(), false) //
            .map { cart: Cart? -> idPrefix + cart!!.id } //
            .collect(Collectors.toList())
        redisSets().operations.delete(keys)
    }

    override fun deleteAll() {
        redisSets().operations.delete(redisSets().members(idPrefix))
    }

    fun findByUserId(id: Long): Optional<Cart> {
        var cartId = redisHash()["carts-by-user-id-idx", id.toString()]
        return findById(cartId)
    }

    companion object {
        private var idPrefix = Cart::class.java.name
        fun getKey(cart: Cart): String {
            return String.format("%s:%s", idPrefix, cart.id)
        }

        fun getKey(id: String?): String {
            return String.format("%s:%s", idPrefix, id)
        }
    }
}