package org.ucomplex.ucomplex.Modules.Subject.SubjectDagger;

import org.ucomplex.ucomplex.Modules.Subject.SubjectTimeline.SubjectTimelineFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * ---------------------------------------------------
 * Created by Sermilion on 19/01/2017.
 * Project: uComplex_v_2
 * ---------------------------------------------------
 * <a href="http://www.ucomplex.org">ucomplex.org</a>
 * <a href="http://www.github.com/sermilion>github</a>
 * ---------------------------------------------------
 */

@Singleton
@Component(modules = {SubjectTimelineModule.class})
public interface SubjectTimelineDiComponent {
    void inject(SubjectTimelineFragment fragment);
}
