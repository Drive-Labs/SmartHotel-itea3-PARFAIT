package com.mehdikerkar.moveup.database;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReservationList {

    public List<Reservation>List = new List<Reservation>() {
        @Override
        public int size() {
            return List.size();
        }

        @Override
        public boolean isEmpty() {
            return List.isEmpty();
        }

        @Override
        public boolean contains( Object o) {
            return List.contains(o);
        }


        @Override
        public Iterator<Reservation> iterator() {
            return List.iterator();
        }


        @Override
        public Object[] toArray() {
            return List.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return List.toArray(a);
        }

        @Override
        public boolean add(Reservation reservation) {
            return List.add(reservation);
        }

        @Override
        public boolean remove( Object o) {
            return List.remove(o);
        }

        @Override
        public boolean containsAll( Collection<?> c) {
            return List.containsAll(c);
        }

        @Override
        public boolean addAll( Collection<? extends Reservation> c) {
            return List.addAll(c);
        }

        @Override
        public boolean addAll(int index,  Collection<? extends Reservation> c) {
            return List.addAll(index, c);
        }

        @Override
        public boolean removeAll( Collection<?> c) {
            return List.removeAll(c);
        }

        @Override
        public boolean retainAll( Collection<?> c) {
            return List.retainAll(c);
        }

        @Override
        public void clear() {

        }

        @Override
        public int hashCode() {
            return List.hashCode();
        }

        @Override
        public Reservation get(int index) {
            return List.get(index);
        }

        @Override
        public Reservation set(int index, Reservation element) {
            return List.set(index, element);
        }

        public void add(int index, Reservation element) {
            List.add(index, element);
        }

        @Override
        public Reservation remove(int index) {
            return List.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return List.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return List.lastIndexOf(o);
        }

        @Override
        public ListIterator<Reservation> listIterator() {
            return List.listIterator();
        }

        @Override
        public ListIterator<Reservation> listIterator(int index) {
            return List.listIterator(index);
        }

        @Override
        public List<Reservation> subList(int fromIndex, int toIndex) {
            return List.subList(fromIndex,toIndex);
        }
    };
}
