# Google Guava
jar_library(name='guava', jars=[
  jar(org='com.google.guava', name='guava', rev='19.0')
])

# Breeze
jar_library(name='breeze', jars=[
  jar(org='org.scalanlp', name='breeze_2.11', rev='0.12')
])

# Apache Commons
jar_library(name='commons-math3', jars=[
  jar(org='org.apache.commons', name='commons-math3', rev='3.6.1')
])

jar_library(name='commons-collections4', jars=[
  jar(org='org.apache.commons', name='commons-collections4', rev='4.1')
])

jar_library(name='commons-io', jars=[
  jar(org='commons-io', name='commons-io', rev='2.5')
])

# Jackson
jar_library(name='jackson', jars=[
  jar(org='com.fasterxml.jackson.core', name='jackson-databind', rev='2.8.2')
])

# Project Lombok
jar_library(name='lombok', jars=[
  jar(org='org.projectlombok', name='lombok', rev='1.16.10')
])

# JUnit
jar_library(name='junit', jars=[
  jar(org='junit', name='junit', rev='4.12'),
  jar(org='org.pantsbuild', name='junit-runner-annotations', rev='0.0.11')
])

# Scalatest
jar_library(name='scalatest',
  jars=[jar(org='org.scalatest', name='scalatest_2.11', rev='3.0.0', excludes=[exclude('org.scala-lang')])],
  dependencies=['//:scala-library', '//:scala-reflect']
)

# Mockito
jar_library(name='mockito', jars=[
  jar(org='org.mockito', name='mockito-all', rev='1.10.19')
])
