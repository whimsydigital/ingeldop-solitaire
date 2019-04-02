find_package(GTest REQUIRED)

add_executable(tests main.cpp)
target_include_directories(tests PRIVATE ${PROJECT_SOURCE_DIR}/lib/)
target_link_libraries(tests libingeldop)
target_link_libraries(tests ${GTEST_BOTH_LIBRARIES})

