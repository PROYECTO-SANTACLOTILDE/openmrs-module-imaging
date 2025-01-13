package org.openmrs.module.imaging;

import java.util.*;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.util.OpenmrsUtil;

public class Studies implements List<DicomStudy> {

    public void sort(Comparator comparator) {
    }

    public static enum STUDIESSTATUS {
        EMPTY,
        UPDATED
    }

    private final List<DicomStudy> studies = new ArrayList<DicomStudy>();

    public Studies() {
    }

    @Override
    public int size() {
        return this.studies.size();
    }

    @Override
    public boolean isEmpty() {
        return this.studies.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.studies.contains(o);
    }

    @Override
    public Iterator<DicomStudy> iterator() {
        return this.studies.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.toArray(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        if (ts.length < this.size()) {
            // Create a new array of the same runtime type
            return (T[]) Arrays.copyOf(this.toArray(), this.size(), ts.getClass());
        }
        // Copy elements into the provided array
        System.arraycopy(this.toArray(), 0, ts, 0, this.size());
        if (ts.length > this.size()) {
            ts[this.size()] = null; // Null-terminate if ts is larger than the collection
        }
        return ts;
    }

    public boolean add(DicomStudy study) {
        return this.studies.add(study);
    }

    @Override
    public boolean remove(Object o) {
        if ( o instanceof DicomStudy) {
            return this.studies.remove(o);
        }
        else {
            return false;
        }
    }

    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(this.studies).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends DicomStudy> collection) {
        if (collection == null || collection.isEmpty()){
            return false;
        }
        for (DicomStudy study: collection) {
            if (study == null) {
                throw new IllegalArgumentException("Collection contains a null DicomStudy");
            }

            // Perform any additional processing for each study here
            // For example: study.setMapped(true); // Assuming a method in DicomStudy
        }

        return this.studies.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends DicomStudy> collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }

        if (i < 0 || i > this.studies.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + i);
        }

        for (DicomStudy study : collection) {
            if (study == null) {
                throw new IllegalArgumentException("Collection contains a null DicomStudy");
            }
            // Perform any additional processing for each study here
        }
        return this.studies.addAll(i, collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }

        boolean removed = false;
        for (Object obj : collection) {
            if (obj instanceof DicomStudy && this.studies.remove(obj)){
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    public void clear() {
        this.studies.clear();
    }

    public DicomStudy getStudy(String studyInstanceUID) {
        for (DicomStudy study : this.studies) {
            if (OpenmrsUtil.nullSafeEquals(study.getStudyInstanceUID(), studyInstanceUID)) {
                return study;
            }
        }
        return null;
    }

    @Override
    public DicomStudy get(int i) {
        if (i < 0 || i >= studies.size()) {
            throw new IndexOutOfBoundsException("Index " + i + " is out of bounds");
        }
        return studies.get(i);
    }

    @Override
    public DicomStudy set(int i, DicomStudy dicomStudy) {
        if (i < 0 || i >= studies.size()) {
            throw new IndexOutOfBoundsException("Index " + i + " is out of bounds");
        }
        return studies.set(i, dicomStudy);
    }

    @Override
    public void add(int i, DicomStudy dicomStudy) {
        if (dicomStudy == null) {
            throw new IllegalArgumentException("DicomStudy cannot be null");
        }
        studies.add(i, dicomStudy);
    }

    @Override
    public DicomStudy remove(int i) {
        if (i < 0 || i >= studies.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + i);
        }
        return studies.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        if ( o instanceof DicomStudy) {
            return studies.lastIndexOf(0);
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof DicomStudy) {
            return studies.lastIndexOf(o);
        }
        return -1; // Return -1 if the object is not a DicomStudy
    }

    @Override
    public ListIterator<DicomStudy> listIterator() {
        return studies.listIterator();
    }

    @Override
    public ListIterator<DicomStudy> listIterator(int i) {
        if (i < 0 || i < studies.size()){
            throw new IndexOutOfBoundsException("Invalid index: " + i);
        }
        return studies.listIterator(i);
    }

    @Override
    public Studies subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > studies.size()) {
            throw new IndexOutOfBoundsException("Invalid range: " + fromIndex + " to " + toIndex);
        }
        return (Studies) studies.subList(fromIndex, toIndex);
    }
}
