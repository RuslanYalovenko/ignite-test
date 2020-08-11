/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.service;

import com.entity.Diff;
import com.entity.DiffResult;
import com.exception.ValidationException;
import com.queue.DiffProducer;
import com.repository.DocStorageIgnite;
import com.util.DiffEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DiffService {

    private static final int DOC_LENGTH = 1000;

    @Autowired
    private DocStorageIgnite docStorage;

    @Autowired
    private DiffProducer diffProducer;

    /**
     * Saves left or right document based on diffId.
     * @param diffId Identifier of Documents pair to compare
     * @param docId Document identifier (left, right)
     * @param doc Document to compare
     * @throws ValidationException
     */
    public void save(long diffId, DiffEnum docId, String doc) throws ValidationException {
        docStorage.saveDoc(diffId, docId, doc);
    }

    /**
     * Locks diff by diffId. Means processing of documents to find diff is in progress.
     * @param diffId Identifier of Documents pair
     */
    private void lock(long diffId) {
        docStorage.saveDiff(diffId, new Diff(true));
    }

    /**
     * Checks size of both documents for diffId
     * @param diffId Identifier of Documents pair
     * @return true if both left and right documents length is less than DOC_LENGTH value
     */
    private boolean isDocSizeSmall(long diffId) {
        return docStorage.isDocLengthLess(diffId, DOC_LENGTH);
    }

    /**
     * Checks if both left and right documents are present for diffId
     * @param diffId
     * @return true if both left and right documents are present for diffId
     */
    private boolean isDiffHasAllDocs(long diffId) {
        return docStorage.isDiffHasAllDocs(diffId);
    }

    /**
     * Makes decision on how to process documents.
     * If for at least one of document has size over specified value processing
     * sends message to a queue to process diff as background process.
     * @param diffId
     * @throws ValidationException
     */
    public void process(long diffId) throws ValidationException {
        if (isDiffHasAllDocs(diffId)) {
            lock(diffId);
            if (isDocSizeSmall(diffId)) {
                compare(diffId);
            } else {
                diffProducer.send(diffId);
            }
        }
    }

    /**
     * Compares two provided documents to find a diff. Saves diff into storage so it is accessible by id.
     * @param diffId
     * @return result of documents comparison after it was placed to a storage. Returned value is used only in tests.
     * @throws ValidationException
     */
    public DiffResult compare(long diffId) throws ValidationException {
        if (!docStorage.isDiffHasAllDocs(diffId)) {
            throw new ValidationException("Both left and right documents should be provided");
        }
        String docLeft = docStorage.getDocument(diffId, DiffEnum.left);
        String docRight = docStorage.getDocument(diffId, DiffEnum.right);

        DiffResult res;
        if (docLeft.length() != docRight.length()) {
            res = new DiffResult("Size is not equal");
        } else if (docLeft.equals(docRight)) {
            res = new DiffResult("Equals");
        } else {
            res = new DiffResult("Different", calculateOffset(docLeft, docRight));
        }

        docStorage.saveDiff(diffId, new Diff(false, res));
        return res;
    }

    /**
     *
     * @param s1 First document
     * @param s2 Second document
     * @return Key - value pairs.
     *  Key is an offset. Position of character in a string which is different.
     *  Value is length. Shows number of symbols which are different in a sequence.
     */
    private Map<Integer, Integer> calculateOffset(String s1, String s2) {
        Map<Integer, Integer> offsetLengthMap = new HashMap<>();
        int offset = -1;
        int length = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.toCharArray()[i] != s2.toCharArray()[i]) {
                if (length == 0) {
                    offset = i;
                    length = 1;
                } else {
                    length += 1;
                }

            } else {
                if (length > 0) {
                    offsetLengthMap.put(offset, length);
                    offset = -1;
                    length = 0;
                }
            }

        }
        if (length > 0) {
            offsetLengthMap.put(offset, length);
        }
        return offsetLengthMap;
    }

    /**
     * Returns diff result based on diffId
     * @param diffId
     * @return
     * @throws ValidationException
     */
    public DiffResult getDiff(long diffId) throws ValidationException {
        Diff diff =  docStorage.getDiffById(diffId);
        if (diff == null) {
            throw new ValidationException("Both left and right documents should be provided");
        }
        if (diff.isLocked()) {
            throw new ValidationException("Diff calculation is in progress");
        }
        return diff.getItem();
    }

}
