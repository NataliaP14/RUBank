package com.example.project3rubank.util;

import java.util.Iterator;

/**
 * This class is used for the list implementation for manipulating the accounts.
 * @param <E> the type of elements stored in this container
 * @author Natalia Peguero, Olivia Kamau
 */
public class List<E> implements Iterable<E> {
	private static final int ARRAY_SIZE = 4;
	private static final int NOT_FOUND = -1;
	private E[] objects;
	private int size;

	/**
	 * This constructor creates a List array object of size 4
	 *
	 */
	public List() {
		this.objects = (E[]) new Object[ARRAY_SIZE];
		this.size = 0;
	} //new array type-casted to E with a capacity of 4.

	/**
	 * Method that finds the index of the account you're searching
	 * @param e object that you're searching for
	 * @return returns the index of the account if it's in the account database
	 */
	private int find(E e) {
		for (int i = 0; i < size; i++) {
			if (e.equals(objects[i])) {
				return i;
			}
		}
		return NOT_FOUND;

	}

	/**
	 * Method that increases the size of the accounts array by four once it reaches its capacity
	 */
	private void grow() {
		E[] newAccount = (E[]) new Object[objects.length + ARRAY_SIZE];

		for (int i = 0; i < size; i++) {
			newAccount[i] = objects[i];
		}
		this.objects = newAccount;
	}

	/**
	 * This method checks whether the account exists
	 * @param e the account we are checking
	 * @return returns true if there account exists, false otherwise
	 */
	public boolean contains(E e) {
		for (int i = 0; i < size; i++) {
			if (e.equals(objects[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method adds a new account at the end of the array
	 * @param e the account to add to the end of the array
	 */
	public void add(E e) {
		//if array is full, increase the size of the array
		if (size == objects.length) {
			grow();
		}
		objects[size] = e;
		size++;
	}

	/**
	 * This method removes an account from the array
	 * @param e the account to remove from the array
	 */
	public void remove(E e) {
		//System.out.println("\nAttempting to remove: " + e);
		int index = find(e);
		//System.out.println("Found at index: " + index);

		if (index != NOT_FOUND) {
			//System.out.println("Removing object at index " + index + ": " + objects[index]);
			// Shift elements to fill the gap
			for (int i = index; i < size - 1; i++) {
				objects[i] = objects[i + 1];
				//System.out.println("Shifted: index " + i + " now contains " + objects[i]);
			}
			objects[size - 1] = null; // Clear the last element
			size--;
		}

	}

	/**
	 * checks whether an account is empty
	 * @return returns true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * checks for the size of the accounts
	 * @return returns the size
	 */
	public int size() {
		return size;
	}

	/**
	 * Used to traverse the list
	 * @return returns the list iterator
	 */
	public Iterator<E> iterator() {

		return new ListIterator<>();
	} //for traversing the list

	/**
	 * This method gets the object at the index from the object list
	 * @param index the index to get the account
	 * @return returns the object of that index is found, if not, then null
	 */
	public E get(int index) {
		if (index >= 0 && index < size) {
				return objects[index];
			}
		return null;
	}

	/**
	 * This method sets the object at a current index
	 * @param index the index to set
	 * @param e the object to move
	 */
	public void set(int index, E e) {
		if (index >= 0 && index < size) {
			objects[index] = e;
		}

	}

	/**
	 * This method gets the index of the object
	 * @param e the object to get the index of
	 * @return returns the index, of account, NOT_FOUND otherwise
	 */
	public int indexOf(E e) {
		for (int i = 0; i < size; i++) {
			if (objects[i].equals(e)) {
				return i;
			}
		}
		return NOT_FOUND;
	}

	/**
	 * This method implementation traverses through the list
	 * @param <E> the type of elements stored in this container
	 */
	private class ListIterator<E> implements Iterator<E> {
		int current = 0; //current index when traversing the list (array)

		public boolean hasNext(){
			return current < size && objects[current] != null;
		} //false if it’s empty or at end of list

		public E next() {
			return (E) objects[current++];
		}
	}
}
