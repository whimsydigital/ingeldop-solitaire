add_library(libingeldop STATIC Ingeldop.cpp 
                               Ingeldop.hpp)

# TODO: add tidy format for clang tidy                           
#find_program(
  #CLANG_TIDY_EXE
  #NAMES "clang-tidy"
  #DOC "Path to clang-tidy executable"
  #)

#set_target_properties(libingeldop PROPERTIES CXX_CLANG_TIDY "${CLANG_TIDY_EXE};-checks=*,-clang-analyzer-alpha.*")
