# Google Guava
jar_library(name='guava', jars=[
  jar(org='com.google.guava', name='guava', rev='19.0')
])

# Breeze
jar_library(name='breeze', jars=[
  jar(org='org.scalanlp', name='breeze_2.12', rev='0.13')
])

# Shapeless
jar_library(name='shapeless', jars=[
  jar(org='com.chuusai', name='shapeless_2.12', rev='2.3.2')
])

# DeepLearning4J
jar_library(name='dl4j', jars=[
  jar(org='org.deeplearning4j', name='deeplearning4j-core', rev='0.7.2')
])

# Json4s
jar_library(name='json4s', jars=[
  jar(org='org.json4s', name='json4s-jackson_2.12', rev='3.5.0')
])

# Scala-ARM
jar_library(name='scala-arm', jars=[
  jar(org='com.jsuereth', name='scala-arm_2.12', rev='2.0')
])

# Java Concurrency In Practice (JCIP) Annotations
jar_library(name='jcip-annotations', jars=[
  jar(org='net.jcip', name='jcip-annotations', rev='1.0')
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
  jars=[jar(org='org.scalatest', name='scalatest_2.12', rev='3.0.1', excludes=[exclude('org.scala-lang')])],
  dependencies=['//:scala-library', '//:scala-reflect']
)

# Mockito
jar_library(name='mockito', jars=[
  jar(org='org.mockito', name='mockito-all', rev='1.10.19')
])
