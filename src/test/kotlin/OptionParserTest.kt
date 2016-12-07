/**
 * Copyright 2016 Laurence Gonsalves
 */
package com.xenomachina.optionparser

import org.junit.Assert
import org.junit.Test

class OptionParserTest {
    @Test
    fun testValuelessShortFlags() {
        class MyOpts(args: Array<String>) {
            private val parser = OptionParser(args)
            val xyz by parser.action<MutableList<String>>("-x", "-y", "-z",
                    help="Really hoopy frood"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name")
                }
            }
        }

        Assert.assertEquals(
                listOf("x", "y", "z", "z", "y"),
                MyOpts(arrayOf("-x", "-y", "-z", "-z", "-y")).xyz)

        Assert.assertEquals(
                listOf("x", "y", "z"),
                MyOpts(arrayOf("-xyz")).xyz)
    }

    @Test
    fun testShortFlagsWithValues() {
        class MyOpts(args: Array<String>) {
            private val parser = OptionParser(args)
            val xyz by parser.actionWithArgument<MutableList<String>>("-x", "-y", "-z",
                    help="Really hoopy frood"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name:$argument")
                }
            }
        }

        // Test with value as separate arg
        Assert.assertEquals(
                listOf("x:0", "y:1", "z:2", "z:3", "y:4"),
                MyOpts(arrayOf("-x", "0", "-y", "1", "-z", "2", "-z", "3", "-y", "4")).xyz)

        // Test with value concatenated
        Assert.assertEquals(
                listOf("x:0", "y:1", "z:2", "z:3", "y:4"),
                MyOpts(arrayOf("-x0", "-y1", "-z2", "-z3", "-y4")).xyz)

        // Test with = between flag and value
        Assert.assertEquals(
                listOf("x:=0", "y:=1", "z:=2", "z:=3", "y:=4"),
                MyOpts(arrayOf("-x=0", "-y=1", "-z=2", "-z=3", "-y=4")).xyz)

        // TODO test with chaining
    }

    @Test
    fun testMixedShortFlags() {
        class MyOpts(args: Array<String>) {
            private val parser = OptionParser(args)
            val myFoo by parser.action<MutableList<String>>("-d", "-e", "-f",
                    help="Foo"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name")
                }
            }
            val myBar by parser.action<MutableList<String>>("-a", "-b", "-c",
                    help="Bar"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name")
                }
            }
        }

        val myOpts = MyOpts(arrayOf("-adbefccbafed"))

        Assert.assertEquals(
                listOf("d", "e", "f", "f", "e", "d"),
                myOpts.myFoo)
        Assert.assertEquals(
                listOf("a", "b", "c", "c", "b", "a"),
                myOpts.myBar)
    }

    @Test
    fun testMixedShortFlagsWithValues() {
        class MyOpts(args: Array<String>) {
            private val parser = OptionParser(args)
            val myFoo by parser.action<MutableList<String>>("-d", "-e", "-f",
                    help="Foo"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name")
                }
            }
            val myBar by parser.action<MutableList<String>>("-a", "-b", "-c",
                    help="Bar"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name")
                }
            }
            val myBaz by parser.actionWithArgument<MutableList<String>>("-x", "-y", "-z",
                    help="Baz"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name:$argument")
                }
            }
        }

        val myOpts = MyOpts(arrayOf("-adecfy5", "-x0", "-bzxy"))

        Assert.assertEquals(
                listOf("a", "c", "b"),
                myOpts.myBar)
        Assert.assertEquals(
                listOf("d", "e", "f"),
                myOpts.myFoo)
        Assert.assertEquals(
                listOf("y:5", "x:0", "z:xy"),
                myOpts.myBaz)
    }

    @Test
    fun testValuelessLongFlags() {
        class MyOpts(args: Array<String>) {
            private val parser = OptionParser(args)
            val xyz by parser.action<MutableList<String>>("--xray", "--yellow", "--zebra",
                    help="Really hoopy frood"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name")
                }
            }
        }

        Assert.assertEquals(
                listOf("xray", "yellow", "zebra", "zebra", "yellow"),
                MyOpts(arrayOf("--xray", "--yellow", "--zebra", "--zebra", "--yellow")).xyz)

        Assert.assertEquals(
                listOf("xray", "yellow", "zebra"),
                MyOpts(arrayOf("--xray", "--yellow", "--zebra")).xyz)
    }

    @Test
    fun testLongFlagsWithValues() {
        class MyOpts(args: Array<String>) {
            private val parser = OptionParser(args)
            val xyz by parser.actionWithArgument<MutableList<String>>("--xray", "--yellow", "--zaphod",
                    help="Xyz"
            ){
                value.orElse{mutableListOf<String>()}.apply {
                    add("$name:$argument")
                }
            }
        }

        // Test with value as separate arg
        Assert.assertEquals(
                listOf("xray:0", "yellow:1", "zaphod:2", "zaphod:3", "yellow:4"),
                MyOpts(arrayOf("--xray", "0", "--yellow", "1", "--zaphod", "2", "--zaphod", "3", "--yellow", "4")).xyz)

        // Test with value concatenated TODO should fail
//        Assert.assertEquals(
//                listOf("xray:0", "yellow:1", "zaphod:2", "zaphod:3", "yellow:4"),
//                MyOpts(arrayOf("--xray0", "--yellow1", "--zaphod2", "--zaphod3", "--yellow4")).xyz)

        // Test with = between flag and value
        Assert.assertEquals(
                listOf("xray:0", "yellow:1", "zaphod:2", "zaphod:3", "yellow:4"),
                MyOpts(arrayOf("--xray=0", "--yellow=1", "--zaphod=2", "--zaphod=3", "--yellow=4")).xyz)
    }

    @Test
    fun testSettingValues() {
        class MyOpts(args: Array<String>) {
            private val parser = OptionParser(args)
            val xyz by parser.actionWithArgument<Int>("-x",
                    help="an integer"
            ){
                argument.toInt()
            }.default(5)
        }

        // Test with no value
        Assert.assertEquals(
                5,
                MyOpts(arrayOf()).xyz)

        // Test with value
        Assert.assertEquals(
                6,
                MyOpts(arrayOf("-x6")).xyz)

        // Test with value as separate arg
        Assert.assertEquals(
                7,
                MyOpts(arrayOf("-x", "7")).xyz)

        // Test with multiple values
        Assert.assertEquals(
                8,
                MyOpts(arrayOf("-x9", "-x8")).xyz)
    }

    // TODO test InvalidOption
    // TODO test short option needs arg at end
    // TODO test long option needs arg at end
}
