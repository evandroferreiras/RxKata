package novoda

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.TestScheduler
import novoda.types.IntegerOperator
import java.util.*
import java.util.concurrent.TimeUnit


class BasicSolutions {

    private val INTEGERS = Arrays.asList(0, 1, 2, 3, 4, 5, 6)

    /**
    - Repeat 3 times every even element and emit once every odd element
    - Send elements until you encounter an odd element then fail, restart and multiply by 2 the entire sequence
    - Prepend the string "Integer : " in front of every element
     */

    fun basicExercise(): Observable<String> {
        return Observable.fromIterable(INTEGERS)
                .flatMap(threeTimesIfEven())
                .flatMap(failIfNotEven())
                .onErrorResumeNext(doubleEverything())
                .map<String>(format())
    }

    private fun doubleEverything(): Function<Throwable, Observable<Int>> {
        return Function { Observable.fromIterable(INTEGERS).map(multiplyBy2()) }
    }

    private fun failIfNotEven(): Function<Int, Observable<Int>> {
        return Function { integer ->
            if (isEven(integer)) {
                Observable.just<Int>(integer)
            } else {
                Observable.error<Int>(IllegalArgumentException())
            }
        }
    }

    private fun threeTimesIfEven(): Function<Int, Observable<Int>> {
        return Function { integer ->
            if (isEven(integer)) {
                Observable.just(integer).repeat(3)
            } else {
                Observable.just<Int>(integer)
            }
        }
    }

    private fun multiplyBy2(): Function<Int, Int> {
        return Function { integer -> integer * 2 }
    }

    private fun format(): Function<Int, String> {
        return Function { integer -> "Integer : " + integer }
    }

    private fun isEven(integer: Int): Boolean {
        return integer % 2 == 0
    }

    private val SENTENCES = Arrays.asList("This is the first sentence", "I want those to be enumerated", "How would you ask?", "That is yours to find out!")

    private val INFINITE_ITERABLE = object : Iterable<Int> {
        override fun iterator(): Iterator<Int> {
            return IntegerOperator()
        }
    }

    /**
    - Get the 20 first integers from INFINITE_ITERABLE
    - Enumerate the SENTENCES by adding their index in front of it.
    - Concatenate the sequences into one line.
     */

    fun infiniteExercise(): Observable<String> {
        return Observable.fromIterable(INFINITE_ITERABLE)
                .take(20)
                .zipWith(Observable.fromIterable(SENTENCES), BiFunction { t1: Int, t2: String -> "" + t1 + ":" + t2 }, false)
                .reduce { t1: String, t2: String -> t1 + " " + t2 }
                .toObservable()
    }


    /**
     * Implement a timer that emits an item every second. Numbers to be emitted: 1 to 6
     */

    fun timer(scheduler: TestScheduler): Observable<Long> {
        return Observable.interval(1, TimeUnit.SECONDS, scheduler)
                .take(10).map { it + 1 }
    }

    /**
     * Implement count() operator (counts the amount of items in an observable)
     * Hint: You can use reduce() operator
     */

    fun count(observable: Observable<Char>): Single<Int> {
        return observable.reduce(0) { sizeSoFar, _ -> sizeSoFar + 1 }
    }
}