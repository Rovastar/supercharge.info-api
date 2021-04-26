package com.redshiftsoft.tesla.dao.feature;

import kdw.common.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/com/redshiftsoft/tesla/dao-context.xml"})
public class FeatureDAO_UT {

    private static final RandomUtils randomUtils = RandomUtils.fast();

    @Resource
    private FeatureDAO featureDAO;

    @Test
    public void insert_getById() {
        // given
        Feature featureIn = randomFeature();

        // when
        featureDAO.insert(featureIn);
        Feature featureOut = featureDAO.getById(featureIn.getId());

        // then
        assertTrue(featureIn.getId() >= 100);
        assertEquals(featureIn.getId(), featureOut.getId());
        assertEquals(featureIn.getTitle(), featureOut.getTitle());
        assertEquals(featureIn.getDescription(), featureOut.getDescription());
        assertEquals(featureIn.getAddedDate(), featureOut.getAddedDate());
        long modifiedDate = featureOut.getModifiedDate().toInstant(ZoneOffset.UTC).toEpochMilli();
        long currentDate = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        long timeDiffMs = Math.abs(modifiedDate - currentDate);
        assertTrue("timeDiffMs=" + timeDiffMs, timeDiffMs < 30_000);

    }

    @Test
    public void update() {
        // given
        Feature featureIn = randomFeature();
        featureDAO.insert(featureIn);

        // when
        featureIn.setDescription(randomUtils.getParagraph(0, 1000, 100, 10, 'a', 'z'));
        LocalDate updatedDateAdded = LocalDate.of(2008, 9, 22);
        featureIn.setAddedDate(updatedDateAdded);
        featureDAO.update(featureIn);

        // then
        Feature featureOut = featureDAO.getById(featureIn.getId());
        assertEquals(featureIn.getId(), featureOut.getId());
        assertEquals(featureIn.getDescription(), featureOut.getDescription());
        assertEquals(featureIn.getTitle(), featureOut.getTitle());
        assertEquals(updatedDateAdded, featureOut.getAddedDate());
    }

    @Test
    public void list() {
        // given
        Feature f1 = randomFeature();
        Feature f2 = randomFeature();
        Feature f3 = randomFeature();
        featureDAO.insert(f1);
        featureDAO.insert(f2);
        featureDAO.insert(f3);

        // when
        List<Feature> list = featureDAO.list();

        // then
        assertTrue(list.size() >= 3);
        assertTrue(list.stream().anyMatch(feature -> Objects.equals(feature.getId(), f1.getId())));
        assertTrue(list.stream().anyMatch(feature -> Objects.equals(feature.getId(), f2.getId())));
        assertTrue(list.stream().anyMatch(features -> Objects.equals(features.getId(), f3.getId())));
    }

    @Test
    public void delete() {
        // given
        Feature feature = randomFeature();
        featureDAO.insert(feature);

        // when
        featureDAO.delete(feature.getId());

        // then
        try {
            featureDAO.getById(feature.getId());
            fail("expected exception");
        } catch (EmptyResultDataAccessException e) {
            /* expected */
        }
    }

    private static Feature randomFeature() {
        Feature feature = new Feature();
        feature.setTitle(randomUtils.getString(1, 99, 'a', 'z'));
        feature.setDescription(randomUtils.getParagraph(1, 10000, 100, 10, 'a', 'z'));
        int randomYear = randomUtils.getInteger(2017, 2020);
        int randomMonth = randomUtils.getInteger(1, 12);
        int randomDay = randomUtils.getInteger(1, 28);
        feature.setAddedDate(LocalDate.of(randomYear, randomMonth, randomDay));
        return feature;
    }

}