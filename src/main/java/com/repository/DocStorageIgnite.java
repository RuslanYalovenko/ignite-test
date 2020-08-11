/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.repository;

import com.entity.Diff;
import com.exception.ValidationException;
import com.util.DiffEnum;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.util.Constants.DIFF_CACHE;
import static com.util.Constants.DOCUMENTS_CACHE;

@Repository
public class DocStorageIgnite {

    private IgniteCache<Long, Map<DiffEnum, String>> documentsCache;
    private IgniteCache<Long, Diff> diffCache;

    @Autowired
    private Ignite ignite;

    @PostConstruct
    public void init() {
        ignite.cluster().active(true);
        documentsCache = ignite.getOrCreateCache(DOCUMENTS_CACHE);
        diffCache = ignite.getOrCreateCache(DIFF_CACHE);
    }


    public boolean isDiffHasAllDocs(long diffId) {
        return documentsCache.containsKey(diffId) &&
                documentsCache.get(diffId).keySet().containsAll(Arrays.asList(DiffEnum.values()));
    }

    public void saveDiff(long diffId, Diff diff) {
        diffCache.put(diffId, diff);
    }

    public String getDocument(long diffId, DiffEnum docId) {
        return documentsCache.get(diffId).get(docId);
    }

    public Diff getDiffById(long diffId) {
        return diffCache.get(diffId);
    }

    public boolean isDocLengthLess(long diffId, int length) {
        return documentsCache.get(diffId).values().stream().allMatch(item -> item.length() < length);
    }

    public void saveDoc(long diffId, DiffEnum docId, String doc) throws ValidationException {
        if (diffCache.get(diffId) != null && diffCache.get(diffId).isLocked()) {
            throw new ValidationException("Documents submitted previously are not processed yet. Please try again later or use different id.");
        }
        if (doc == null) {
            throw new ValidationException("Document should be provided");
        }
        Map<DiffEnum, String> map = documentsCache.get(diffId);
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(docId, doc);
        documentsCache.put(diffId, map);
    }

}
