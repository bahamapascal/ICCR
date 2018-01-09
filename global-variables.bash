#!/bin/bash
#Script to initiate common variables that are used globally.

version=1.0.0
user=$USER
group=$(id -Gn $user | awk '{print $1}')
dir=$PWD/dist/temp
iccrdir=$dir/iccr
dist=$PWD/dist
ICC_PROD_DIR=~/projects/extjs/iccw/build/production/icc

export user group dir iccrdir dist version ICC_PROD_DIR
