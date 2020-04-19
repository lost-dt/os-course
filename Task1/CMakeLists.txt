cmake_minimum_required(VERSION 3.3)
project(hello)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp Allocator.cpp Allocator.h)
add_executable(os-task-1 ${SOURCE_FILES})
