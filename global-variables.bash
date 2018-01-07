#!/bin/bash
#Script to initiate common variables that are used globally.

version=1.0.0
user=$USER
group=$(id -Gn $user | awk '{print $1}')
dir=$PWD/dist/temp
iccrdir=$dir/iccr
dist=$PWD/dist

export user group dir iccrdir dist version
