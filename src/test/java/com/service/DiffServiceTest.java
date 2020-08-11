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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DiffService.class})
public class DiffServiceTest {

    @Autowired
    private DiffService diffService;

    @MockBean
    private DocStorageIgnite docStorage;

    @MockBean
    private DiffProducer diffProducer;


    @Test(expected = ValidationException.class)
    public void compare_NoDocs() throws Exception {
        when(docStorage.isDiffHasAllDocs(1)).thenReturn(false);
        doNothing().when(docStorage).saveDiff(isA(Long.class), isA(Diff.class));
        diffService.compare(1);
    }

    @Test
    public void compare_DocsEqual() throws Exception {
        when(docStorage.isDiffHasAllDocs(1)).thenReturn(true);
        when(docStorage.getDocument(1, DiffEnum.left)).thenReturn("Text");
        when(docStorage.getDocument(1, DiffEnum.right)).thenReturn("Text");
        doNothing().when(docStorage).saveDiff(isA(Long.class), isA(Diff.class));
        DiffResult diff = diffService.compare(1);
        Assert.assertEquals("Values should be equal", "Equals", diff.getStatus());
    }

    @Test
    public void compare_DocsDifferentLength() throws Exception {
        when(docStorage.isDiffHasAllDocs(1)).thenReturn(true);
        when(docStorage.getDocument(1, DiffEnum.left)).thenReturn("Text");
        when(docStorage.getDocument(1, DiffEnum.right)).thenReturn("Text2");
        doNothing().when(docStorage).saveDiff(isA(Long.class), isA(Diff.class));
        DiffResult diff = diffService.compare(1);
        Assert.assertEquals("Should be different length", "Size is not equal", diff.getStatus());
    }

    @Test
    public void compare_DocsSameLengthDifferentLastChar() throws Exception {
        when(docStorage.isDiffHasAllDocs(1)).thenReturn(true);
        when(docStorage.getDocument(1, DiffEnum.left)).thenReturn("Text1");
        when(docStorage.getDocument(1, DiffEnum.right)).thenReturn("Text2");
        doNothing().when(docStorage).saveDiff(isA(Long.class), isA(Diff.class));
        DiffResult diff = diffService.compare(1);
        DiffResult expected = new DiffResult("Different");
        Map<Integer, Integer> map = new HashMap<>();
        map.put(4,1);
        expected.setOffset(map);
        Assert.assertEquals("Should be different content", expected, diff);
    }

    @Test
    public void compare_DocsSameLengthDifferentFirstChar() throws Exception {
        when(docStorage.isDiffHasAllDocs(1)).thenReturn(true);
        when(docStorage.getDocument(1, DiffEnum.left)).thenReturn("1Text");
        when(docStorage.getDocument(1, DiffEnum.right)).thenReturn("2Text");
        doNothing().when(docStorage).saveDiff(isA(Long.class), isA(Diff.class));
        DiffResult diff = diffService.compare(1);
        DiffResult expected = new DiffResult("Different");
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,1);
        expected.setOffset(map);
        Assert.assertEquals("Should be different content", expected, diff);
    }

    @Test
    public void compare_DocsSameLengthDifferentAll() throws Exception {
        when(docStorage.isDiffHasAllDocs(1)).thenReturn(true);
        when(docStorage.getDocument(1, DiffEnum.left)).thenReturn("12345");
        when(docStorage.getDocument(1, DiffEnum.right)).thenReturn("34567");
        doNothing().when(docStorage).saveDiff(isA(Long.class), isA(Diff.class));
        DiffResult diff = diffService.compare(1);
        DiffResult expected = new DiffResult("Different");
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,5);
        expected.setOffset(map);
        Assert.assertEquals("Should be different content", expected, diff);
    }

    @Test
    public void compare_DocsSameLengthDifferentMultiple() throws Exception {
        when(docStorage.isDiffHasAllDocs(1)).thenReturn(true);
        when(docStorage.getDocument(1, DiffEnum.left)).thenReturn("1122211333311");
        when(docStorage.getDocument(1, DiffEnum.right)).thenReturn("1133311444411");
        doNothing().when(docStorage).saveDiff(isA(Long.class), isA(Diff.class));
        DiffResult diff = diffService.compare(1);
        DiffResult expected = new DiffResult("Different");
        Map<Integer, Integer> map = new HashMap<>();
        map.put(2,3);
        map.put(7,4);
        expected.setOffset(map);
        Assert.assertEquals("Should be different content", expected, diff);
    }

    @Test(expected = ValidationException.class)
    public void getDiff_Null() throws ValidationException {
        when(docStorage.getDiffById(1)).thenReturn(null);
        DiffResult res = diffService.getDiff(1);
    }

    @Test(expected = ValidationException.class)
    public void getDiff_Locked() throws ValidationException {
        when(docStorage.getDiffById(1)).thenReturn(new Diff(true));
        DiffResult res = diffService.getDiff(1);
    }

    @Test
    public void getDiff_Ok() throws ValidationException {
        when(docStorage.getDiffById(1)).thenReturn(new Diff(false, new DiffResult("Equals")));
        DiffResult res = diffService.getDiff(1);
        Assert.assertEquals("Values should be equal", "Equals", res.getStatus());
    }

}