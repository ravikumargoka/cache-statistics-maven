package com.ravi.cache.statistics.manager;

import com.ravi.cache.statistics.entity.Teacher;
import com.ravi.cache.statistics.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.List;

import static com.ravi.cache.statistics.constants.CacheConstants.TEACHERS_CACHE_ALIAS;
import static com.ravi.cache.statistics.constants.CacheConstants.TEACHER_CACHE_KEY;

@Component
@Slf4j
public class TeacherServiceManager {

    private final TeacherService teacherService;

    private final CacheManager cacheManager;

    public TeacherServiceManager(TeacherService teacherService, CacheManager cacheManager) {
        this.teacherService = teacherService;
        this.cacheManager = cacheManager;
    }

    public List<Teacher> getAllTeachers() {
        if (log.isDebugEnabled()) {
            log.debug("START :: Getting all teachers");
        }
        List<Teacher> teachers;
        Cache<String, Object> teachersCache = cacheManager.getCache(TEACHERS_CACHE_ALIAS, String.class, Object.class);
        teachers = (List<Teacher>) teachersCache.get(TEACHER_CACHE_KEY);
        if (CollectionUtils.isEmpty(teachers)) {
            if (log.isDebugEnabled()) {
                log.debug("Data Not found in Cache");
                log.debug("Reading user information from service/DB");
            }
            teachers = teacherService.getAllTeachers();
            teachersCache.put(TEACHER_CACHE_KEY, teachers);
        }
        if (log.isDebugEnabled()) {
            log.debug("END :: Getting all teachers: {}", teachers);
        }
        return teachers;
    }

    public Teacher getTeacherById(Long id) {
        if (log.isDebugEnabled()) {
            log.debug("START :: Getting teacher with id: {}", id);
        }
        Teacher teacher;
        Cache<String, Object> teacherCache = cacheManager.getCache(TEACHERS_CACHE_ALIAS, String.class, Object.class);
        teacher = (Teacher) teacherCache.get(String.valueOf(id));
        if (null == teacher) {
            if (log.isDebugEnabled()) {
                log.debug("Data Not found in Cache for teacher with id: {}", id);
                log.debug("Reading user information from service/DB");
            }
            teacher = teacherService.getTeacherById(id);
            teacherCache.put(String.valueOf(id), teacher);
        }
        if (log.isDebugEnabled()) {
            log.debug("END :: Getting teacher with id: {}", id);
        }
        return teacher;
    }

    public Teacher createTeacher(Teacher entity) {
        if (log.isDebugEnabled()) {
            log.debug("START :: Create teacher: {}", entity);
        }
        Teacher teacher;
        Cache<String, Object> teacherCache = cacheManager.getCache(TEACHERS_CACHE_ALIAS, String.class, Object.class);
        teacher = teacherService.createTeacher(entity);
        //Update the cache with latest list
        List<Teacher> teachers = teacherService.getAllTeachers();
        teacherCache.put(TEACHER_CACHE_KEY, teachers);
        if (log.isDebugEnabled()) {
            log.debug("END :: Create teacher: {}", entity);
        }
        return teacher;
    }

    public Teacher updateTeacher(Teacher entity) {
        if (log.isDebugEnabled()) {
            log.debug("START :: Update teacher: {}", entity);
        }
        Teacher teacher;
        Cache<String, Object> teacherCache = cacheManager.getCache(TEACHERS_CACHE_ALIAS, String.class, Object.class);
        teacher = teacherService.updateTeacher(entity);
        //Update the cache with latest list
        List<Teacher> teachers = teacherService.getAllTeachers();
        teacherCache.put(TEACHER_CACHE_KEY, teachers);
        if (log.isDebugEnabled()) {
            log.debug("END :: Update teacher: {}", entity);
        }
        return teacher;
    }

    public void deleteTeacherById(Long id) {
        if (log.isDebugEnabled()) {
            log.debug("START :: Deleting teacher with id: {}", id);
        }
        teacherService.deleteTeacherById(id);
        Cache<String, Object> teacherCache = cacheManager.getCache(TEACHERS_CACHE_ALIAS, String.class, Object.class);
        //Update the cache with latest list
        List<Teacher> teachers = teacherService.getAllTeachers();
        teacherCache.put(TEACHER_CACHE_KEY, teachers);
        if (log.isDebugEnabled()) {
            log.debug("END :: Deleting teacher with id: {}", id);
        }
    }

}
