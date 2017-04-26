package jfnlite;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.util.List;
import java.util.ArrayList;

public class Fn {

	/**
	 * Represents a function that accepts one argument and produces a result.
	 */
	public static interface Function<T,R> {
		public R apply(T t);
	}

	/**
	 * Represents a function that accepts two arguments and produces a result.
	 */
	public static interface BiFunction<T,U,R> {
		public R apply(T t, U u);
	}

	/**
	 * Represents a predicate (boolean-valued function) of one argument.
	 */
	public static interface Predicate<T> {
		public boolean test(T t);
	}

	/**
	 * Represents a predicate (boolean-valued function) of two arguments. This is the two-arity specialization of Predicate.
	 */
	public static interface BiPredicate<T,U> {
		public boolean test(T t, U u);
	}

	/**
	 * Represents an operation that accepts a single input argument and returns no result. Unlike most other functional interfaces, Consumer is expected to operate via side-effects.
	 */
	public static interface Consumer<T> {
		public void accept(T t);
	}

	/**
	 * Represents an operation that accepts two input arguments and returns no result. This is the two-arity specialization of Consumer. Unlike most other functional interfaces, BiConsumer is expected to operate via side-effects.
	 */
	public static interface BiConsumer<T,U> {
		public void accept(T t, U u);
	}

	/**
	 * Returns an iterator consisting of the results of applying the given function to the elements of a given iterator.
	 */
	public static <T,R> Iterator<R> map(final Iterator<T> iterator, final Function<T,R> f) {
		return new Iterator<R>() {
			public R next() { return f.apply((iterator.next())); };
			public boolean hasNext() { return iterator.hasNext(); };
			public void remove() { iterator.remove(); };
		};
	}

	/**
	 * Returns an iterable consisting of the results of applying the given function to the elements of a given iterable.
	 */
	public static <T,R> Iterable<R> map(final Iterable<T> iterable, final Function<T,R> f) {
		return new Iterable<R>(){
			public Iterator<R> iterator() { return map(iterable.iterator(), f); }
		};
	}

	/**
	 * Performs a reduction on the elements of the provided iterator, using the provided identity value and an associative accumulation function, and returns the reduced value.
	 */
	public static <T,U> U reduce(final Iterator<T> iterator, U identity, final BiFunction<U,T,U> accumulator) {
		U accumulated = identity;
		while(iterator.hasNext()) {
			accumulated = accumulator.apply(accumulated, iterator.next());
		}
		return accumulated;
	}

	/**
	 * Performs a reduction on the elements of the provided iterable, using the provided identity value and an associative accumulation function, and returns the reduced value.
	 */
	public static <T,U> U reduce(final Iterable<T> iterable, U identity, final BiFunction<U,T,U> accumulator) {
		return reduce(iterable.iterator(), identity, accumulator);
	}

	/**
	 * Returns an iterator consisting of the elements of this iterator that match the given predicate.
	 */
	public static <T> Iterator<T> filter(final Iterator<T> iterator, final Predicate<T> p) {
		return new Iterator<T>() {
			private T cachedFilteredNext = null;
			public T next() {
				T filteredNext = null;
				if(this.hasNext()) {
					filteredNext = cachedFilteredNext;
					cachedFilteredNext = null;
				} else {
					throw(new NoSuchElementException());
				}
				return filteredNext;
			};
			public boolean hasNext() {
				T readNext = null;
				while((cachedFilteredNext == null) && iterator.hasNext()){
					readNext = iterator.next();
					if(p.test(readNext)) cachedFilteredNext = readNext;
				}
				return (cachedFilteredNext != null);
			};
			public void remove() { throw(new UnsupportedOperationException()); };
		};
	}

	/**
	 * Returns an iterable consisting of the elements of this iterable that match the given predicate.
	 */
	public static <T> Iterable<T> filter(final Iterable<T> iterable, final Predicate<T> p) {
		return new Iterable<T>(){
			public Iterator<T> iterator() { return filter(iterable.iterator(), p); }
		};
	}

	/**
	 * Stores the elements from the Iterator into a List
	 */
	public static <T> List<T> collectToList(final Iterator<T> iterator) {
		List<T> list = new ArrayList<T>();
		while(iterator.hasNext()) {
			list.add(iterator.next());
		};
		return list;
	}

	/**
	 * Stores the elements from the Iterable into a List
	 */
	public static <T> List<T> collectToList(final Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		for(T element: iterable) {
			list.add(element);
		};
		return list;
	}

	/**
	 * Returns an Identity Iterator (which contains no elements).
	 * 
	 * Although the most efficient approach is to do a class cast on a constant iterator,
	 * using the generic-enabled constructor of this class is a way to avoid unchecked cast warnings
	 */
	public static <T> Iterator<T> getIdentityIterator() {
		return new Iterator<T>(){
			public T next() { throw(new NoSuchElementException()); };
			public boolean hasNext() { return false; };
			public void remove() { throw(new UnsupportedOperationException()); };
		};
	}
	
	/**
	 * Creates a lazily concatenated iterator whose elements are all the elements of the input iterators.
	 * The resulting iterator is ordered if all the input iterators are ordered.
	 * 
	 * Note that this function does not exist in java8 Stream (although the same could be done passing an identity function to flatMap function),
	 * however, I already needed to implement this functionality as a requirement to flatMap, so why not making it public as it is done in Scala?
	 */
	public static <T> Iterator<T> flatten(final Iterator<Iterator<T>> iteratorOfIterators) {
		return new Iterator<T>() {
			// The new Iterator will need a currentIterator variable to keep track of which of all iterators is in use
			private Iterator<T> currentIterator = getIdentityIterator(); // We initialize it to an empty iterator
			public T next() {
				if(this.hasNext()) {
					return currentIterator.next();
				} else {
					throw(new NoSuchElementException());
				}
			};
			public boolean hasNext() {
				while( (!currentIterator.hasNext()) && (iteratorOfIterators.hasNext()) ) {
					currentIterator = iteratorOfIterators.next();
				};
				return currentIterator.hasNext();
			};
			public void remove() { throw new UnsupportedOperationException(); };
		};
	}

	/**
	 * Creates a lazily concatenated iterable whose elements are all the elements of the input iterables.
	 * The resulting iterable is ordered if all the input iterables are ordered.
	 * 
	 * Note that this function does not exist in java8 Stream (although the same could be done passing an identity function to flatMap function),
	 * however, I already needed to implement this functionality as a requirement to flatMap, so why not making it public as it is done in Scala?
	 */
	public static <T> Iterable<T> flatten(final Iterable<Iterable<T>> iterableOfIterables) {
		Function<Iterable<T>,Iterator<T>> iterableToIterator = new Function<Iterable<T>,Iterator<T>>() {
			public Iterator<T> apply(Iterable<T> iterable) {
				return iterable.iterator();
			};
		};
		final Iterable<Iterator<T>> iterableOfIterators = map(iterableOfIterables, iterableToIterator);
		return new Iterable<T>(){
			public Iterator<T> iterator() {
				return flatten(iterableOfIterators.iterator());
			}
		};
	}

	/**
	 * Creates a lazily concatenated iterator whose elements are all the elements of the two input iterators.
	 * The resulting iterator is ordered if both input iterators are ordered.
	 */
	public static <T> Iterator<T> concat(final Iterator<T> iterator1, final Iterator<T> iterator2) {
		return new Iterator<T>() {
			public T next() {
				if(iterator1.hasNext()) {
					return iterator1.next();
				} else {
					return iterator2.next();
				}
			};
			public boolean hasNext() {
				if(iterator1.hasNext()) {
					return true;
				} else {
					return iterator2.hasNext();
				}
			};
			public void remove() { throw new UnsupportedOperationException(); };
		};
	}

	/**
	 * Creates a lazily concatenated iterable whose elements are all the elements of the two input iterables.
	 * The resulting iterator is ordered if both input iterables are ordered.
	 */
	public static <T> Iterable<T> concat(final Iterable<T> iterable1, final Iterable<T> iterable2) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return concat(iterable1.iterator(), iterable2.iterator());
			}
		};
	}

	/**
	 * Creates an Iterator returning the values from an array of objects
	 * 
	 * @param	array	an array of objects
	 * @return			the newly created Iterator object
	 */
	public static <T> Iterator<T> iteratorOf(final T[] array) {
		final int[] indexWrapper = {0};
		return new Iterator<T>() {
			public T next() {
				if(this.hasNext()) {
					return array[indexWrapper[0]++];
				} else {
					throw(new NoSuchElementException());
				}
			};
			public boolean hasNext() {
				return (indexWrapper[0] < array.length);
			};
			public void remove() { throw(new UnsupportedOperationException()); };
		};
	}

	/**
	 * Creates an Iterable returning the values from an array of objects
	 * 
	 * @param	array	an array of objects
	 * @return			the newly created Iterable object
	 */
	public static <T> Iterable<T> iterableOf(final T[] array) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return iteratorOf(array);
			};
		};
	}
	
	/**
	 * Creates an Iterator containing a single element
	 * 
	 * @param	singleElement	a single element
	 * @return					the newly created Iterator object
	 */
	public static <T> Iterator<T> iteratorOf(final T singleElement) {
		final boolean[] hasNextWrapper = {true};
		return new Iterator<T>() {
			public T next() {
				if(this.hasNext()) {
					hasNextWrapper[0] = false;
					return singleElement;
				} else {
					throw(new NoSuchElementException());
				}
			};
			public boolean hasNext() {
				return (hasNextWrapper[0]);
			};
			public void remove() { throw(new UnsupportedOperationException()); };
		};
	}

	/**
	 * Creates an Iterable containing a single element
	 * 
	 * @param	singleElement	a single element
	 * @return					the newly created Iterator object
	 */
	public static <T> Iterable<T> iterableOf(final T singleElement) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return iteratorOf(singleElement);
			};
		};
	}

}
