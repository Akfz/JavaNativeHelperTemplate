cmake_minimum_required(VERSION 3.10)
project(mytest LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(mytest SHARED native_impl.cpp)

if(WIN32)
    set_target_properties(mytest PROPERTIES PREFIX "")
    target_link_options(mytest PRIVATE -static-libgcc -static-libstdc++)

    if(NOT DEFINED CROSS_JNI_INCLUDE_DIRS AND DEFINED ENV{CROSS_JNI_INCLUDE_DIRS})
        set(CROSS_JNI_INCLUDE_DIRS "$ENV{CROSS_JNI_INCLUDE_DIRS}")
    endif()

    if(DEFINED CROSS_JNI_INCLUDE_DIRS)
        message(STATUS "Using cross-compile JNI headers: ${CROSS_JNI_INCLUDE_DIRS}")
        foreach(dir ${CROSS_JNI_INCLUDE_DIRS})
            target_include_directories(mytest PRIVATE ${dir})
        endforeach()
    endif()

else()
    set_target_properties(mytest PROPERTIES LINK_FLAGS "-Wl,-z,noexecstack")

    find_package(JNI)

    if(JNI_FOUND AND JNI_INCLUDE_DIRS)
        message(STATUS "Found JNI via CMake: ${JNI_INCLUDE_DIRS}")
        target_include_directories(mytest PRIVATE ${JNI_INCLUDE_DIRS})
    else()
        message(STATUS "CMake FindJNI incomplete, using manual fallback for headers.")
        if(DEFINED ENV{JAVA_HOME})
            target_include_directories(mytest PRIVATE
                "$ENV{JAVA_HOME}/include"
                "$ENV{JAVA_HOME}/include/linux"
            )
        else()
            target_include_directories(mytest PRIVATE
                "/usr/lib/jvm/java-17-openjdk-amd64/include"
                "/usr/lib/jvm/java-17-openjdk-amd64/include/linux"
            )
        endif()
    endif()

    if(EXISTS "/headers")
        message(STATUS "Adding mounted JNI headers from /headers")
        target_include_directories(mytest PRIVATE /headers)
    endif()
endif()

if(DEFINED JNI_HEADERS)
    set(HEADERS_DIR ${JNI_HEADERS})
else()
    set(HEADERS_DIR ${CMAKE_SOURCE_DIR}/../../../build/generated/jni-headers)
endif()

if(EXISTS ${HEADERS_DIR})
    target_include_directories(mytest PRIVATE ${HEADERS_DIR})
endif()