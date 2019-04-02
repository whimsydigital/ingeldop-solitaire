set(CURSES_NEED_WIDE TRUE)
find_package(Curses REQUIRED)

add_executable(ingeldop-cli main.cpp)
target_include_directories(ingeldop-cli PRIVATE ${PROJECT_SOURCE_DIR}/lib/)
target_include_directories(ingeldop-cli PRIVATE ${CURSES_INCLUDE_DIR})
target_link_libraries(ingeldop-cli libingeldop)
target_link_libraries(ingeldop-cli ${CURSES_LIBRARY})

