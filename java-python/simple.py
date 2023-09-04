import jpype
from jpype.types import *

jpype.startJVM(classpath=["./"])

Simple = JClass('Simple')

print(Simple.getNumber())
