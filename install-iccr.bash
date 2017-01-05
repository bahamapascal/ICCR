#!/bin/bash

ver=1.0.6
pkg=iccr-${ver}.tgz
dir=/opt
iccrDir=$dir/iccr
iccrPropDir=$iccrDir/conf
iccrPropFile=$iccrPropDir/iccr.properties
iccrApiKeyProp=iccrApiKey
iotaDir=$dir/iota
curDir=`pwd`
what=ICCR
doesYum=0
doesApt=0
hasJava=1
hasRightJava=1
requiredJavaVersion=1.8
doWhat=install

if [ ! -f $pkg ]; then
    echo "Failed to find the required ICCR archive file ($pkg) in the local directory!"
    echo "I can't continue"
    exit
fi

echo
echo "This script will install the $what into $iccrDir"
echo
echo "If that directory does not exist, it will be created"
echo
echo "Does your login account have permission to create the new directory $iccrDir if needed? [Y/n]"

havePerm=Y
read havePerm

echo
echo "Checking for required version of java..."
echo
which=`which java`
rval=$?
if [ "${rval}" = "1" ]; then
    hasJava=0
else
    hasJava=1
fi

if [ "${hasJava}" = "1" ]; then
    set -- $(java -fullversion 2>&1 | sed -e 's/\"//g')
    curJavaVersion=$4
    echo
    echo Found java version $curJavaVersion
    echo

    hasRightJava=0

    [[ ${curJavaVersion} == ${requiredJavaVersion}* ]] && hasRightJava=1
fi

which=`which yum`
rval=$?
if [ "${rval}" = "0" ]; then
    doesYum=1
else
    which=`which apt-get`
    rval=$?
    if [ "${rval}" = "0" ]; then
        doesApt=1
    fi
fi

echo "hasJava $hasJava, hasRightJava $hasRightJava"

if [ "${hasJava}" = "0" -o "${hasRightJava}" = "0" ]; then
    if [ "${hasJava}" = "0" ]; then
        echo "Failed to find java, it is required to run ${what}!"
        echo

        if [ "${doesApt}" = "0" -a "${doesYum}" = "0" ]; then
            echo
            echo "Did not find a package manager (i.e. yum or apt-get) to install java!"
            echo "Please install java first, then run this installer"
            echo
            exit
        fi

        echo
        echo "Would you like to install java? [Y/n]"
        installJava=Y
        read installJava

        echo
        if [ "${installJava}" = "n" ]; then
            echo
            echo "Ok, please install java first, then run this installer"
            echo
            exit
        fi
    else
        echo "Failed to find the required version of java!"
        echo "This machine has $curJavaVersion, but version $requiredJavaVersion is required to run ${what}!"
        echo

        if [ "${doesApt}" = "0" -a "${doesYum}" = "0" ]; then
            echo
            echo "Did not find a package manager (i.e. yum or apt-get) to upgrade java!"
            echo "Please upgrade java first, then run this installer"
            echo
            exit
        fi

        echo
        echo "Would you like to upgrade java? [Y/n]"
        upgradeJava=Y
        read upgradeJava

        echo
        if [ "${upgradeJava}" = "n" ]; then
            echo
            echo "Ok, please install java version $requiredJavaVersion first, then run this installer"
            echo
            exit
        fi
        doWhat=upgrade
    fi

    if [ "${installJava}" != "n" -a "${upgradeJava}" != "n" ]; then
       echo
       echo "Ok, attempting to $doWhat java..."
       echo
       if [ "${doesApt}" = "1" ]; then
           if [ "${doWhat}" = "install" ]; then
               echo "sudo apt-get install java"
           else
               echo "sudo apt-get upgrade java"
           fi
       else
           if [ "${doesYum}" = "1" ]; then
               if [ "${doWhat}" = "install" ]; then
                   echo "sudo yum install java"
               else
                   echo "sudo yum upgrade java"
               fi
           fi
       fi
       echo
       echo "Ok, java $doWhat attempt finished"
       echo
   fi
fi

echo "This script will install the ICCR software into $iccrDir"
echo
echo "If that directory does not exist, it will be created"
echo
echo "Does your login account have permission to create the new directory $iccrDir if needed? [Y/n]"

havePerm=Y
read havePerm

todo
echo alrighty then...
exit

if [ ! -d $dir ]; then
    echo "Creating $dir"
    if [ "${havePerm}" = "n" ]; then
        sudo mkdir $dir
        sudo chmod a+x $dir
        sudo chmod a+r $dir
        sudo chmod a+w $dir
    else
        mkdir $dir
    fi
fi

if [ ! -d $iccrDir ]; then
    echo "Creating $iccrDir"
    if [ "${havePerm}" = "n" ]; then
        sudo mkdir $iccrDir
        sudo chmod a+x $iccrDir
        sudo chmod a+r $iccrDir
        sudo chmod a+w $iccrDir
    else
        mkdir $iccrDir
    fi
fi

if [ ! -d $iotaDir ]; then
    echo "Creating $iotaDir"
    if [ "${havePerm}" = "n" ]; then
        sudo mkdir $iotaDir
        sudo chmod a+x $iotaDir
        sudo chmod a+r $iotaDir
        sudo chmod a+w $iotaDir
    else
        mkdir $iotaDir
    fi
fi


echo
echo "cd $dir"
cd $dir

echo
echo "tar -xzvf $curDir/$pkg"
echo

tar -xzvf $curDir/$pkg

echo
echo "Would you like to set the $what API access key (password)? [Y/n]"
setPwd=Y
read setPwd

if [ "${setPwd}" = "n" ]; then
    apiKey=`grep $iccrApiKeyProp $iccrPropFile | sed -e "s/${iccrApiKeyProp}=//g"`
    echo "Ok, using the default $what API access key value: $apiKey"
    echo
else
    echo
    echo "Ok, enter a new value for the $what API access key, then press 'enter':"
    read apiKey
    echo
    echo "Ok, using $apiKey as the $what API access key..."
    `sed -i "s/^${iccrApiKeyProp}=.*$/${iccrApiKeyProp}=${apiKey}/g" $iccrPropFile`
fi

echo
echo "Done"
echo 




