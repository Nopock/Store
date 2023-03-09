/*
 * MIT License
 *
 * Copyright (c) 2023 Nathan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.hyrical.store.debugging

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.logging.Logger

class RepositoryDebuggingHandler(private val target: Any, private val logger: Logger) : InvocationHandler {

	private val methods = mutableMapOf<String, Method>()

	init {
		for (method in target.javaClass.declaredMethods) {
			methods[method.name] = method
		}
	}
	override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
		method?.let {
			val startTime = System.currentTimeMillis()
			val result = methods[it.name]?.invoke(target, *(args ?: emptyArray()))

			logger.info("It took ${System.currentTimeMillis() - startTime} ms to execute ${it.name} on the ${target.javaClass.simpleName} class.")

			return result!!
		} ?: throw IllegalArgumentException("Method parameter is null.")
	}

}
