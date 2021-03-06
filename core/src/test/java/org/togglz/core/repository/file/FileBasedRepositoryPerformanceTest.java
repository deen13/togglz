package org.togglz.core.repository.file;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;

public class FileBasedRepositoryPerformanceTest {

    private File file;
    private FileBasedStateRepository repository;

    @BeforeEach
    public void before() throws Exception {

        // create repository
        file = File.createTempFile(this.getClass().getSimpleName(), null);
        repository = new FileBasedStateRepository(file);

        // configure the state for EXISTING
        repository.setFeatureState(new FeatureState(PerformanceFeatures.EXISTING).enable());

        // read it once for warming up the cache
        repository.getFeatureState(PerformanceFeatures.EXISTING);

    }

    @AfterEach
    public void after() throws Exception {
        file.delete();
    }

    @Test
    public void readingExistingFeature() {
        runPerformanceTest(PerformanceFeatures.EXISTING);
    }

    @Test
    public void readingMissingFeature() {
        runPerformanceTest(PerformanceFeatures.MISSING);
    }

    private void runPerformanceTest(Feature feature) {

        for (int l = 0; l < 6; l++) {

            long start = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                repository.getFeatureState(feature);
            }
            long time = System.currentTimeMillis() - start;

            System.out.println("Time for " + feature.name() + ": " + time);
        }
    }

    private enum PerformanceFeatures implements Feature {
        EXISTING,
        MISSING;
    }
}
