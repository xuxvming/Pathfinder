package com.group12.pathfinder

import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class PathFinderFactoryTest {

    private var factory: PathFinderFactory? = null

    @Before
    fun setup() {
        factory = PathFinderFactory()
    }

    @Test
    fun testGetAbstractPathFinder() {
        val `object` = factory!!.pathFinder
        assertTrue(`object` is AbstractPathFinder)
    }
}
