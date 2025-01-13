package org.openmrs.module.imaging.extension.html;

import org.openmrs.module.imaging.api.study.DicomStudy;

import java.util.Comparator;

public class StudyComparator implements Comparator<DicomStudy> {

    private final Comparator comparator;

    public StudyComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(DicomStudy dicomStudy1, DicomStudy dicomStudy2) {
        DicomStudy obj1 = null;
        DicomStudy obj2 = null;
        if (dicomStudy1 != null && dicomStudy1.getMrsPatient() != null) {
            obj1 = dicomStudy1;
        }
        if (dicomStudy2 != null && dicomStudy2.getMrsPatient() != null) {
            obj2 = dicomStudy2;
        }

        assert obj1 != null;
        assert obj2 != null;
        return comparator.compare(obj1.getStudyInstanceUID(), obj2.getStudyInstanceUID());
    }
}
