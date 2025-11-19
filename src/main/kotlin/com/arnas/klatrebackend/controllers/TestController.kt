package com.arnas.klatrebackend.controllers

import org.springframework.dao.DataAccessException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/test")
class TestController {


    @RequestMapping("/exception")
    fun hello(@RequestParam name: String): String {
        throwException()
        return "Hello World"
    }


    fun throwException() {
        //throw Exception("This is an exception")
        throw Exception("this is an exception")
    }
}