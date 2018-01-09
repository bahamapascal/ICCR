#!/bin/bash

#import common enviornment variables.
source global-variables.bash

# Support for API access key complexity:
r1='^.*[A-Z].*$'
r2='^.*[a-z].*$'
r3='^.*[0-9].*$'
r4='^.*[!@#$&_].*$'
r5='^.{6,}'

function checkApiKeyComplexity {
    [[ $1 =~ $r1 ]] && [[ $1 =~ $r2 ]] && [[ $1 =~ $r3 ]] && [[ $1 =~ $r4 ]] && [[ $1 =~ $r5 ]]
}

function printApiKeyHelp {
    echo "Enter an API access key with this complexity:"
    echo "- at least 6 characters in length"
    echo "- at least one upper case character"
    echo "- at least one lower case character"
    echo "- at least one numeric digit"
    echo '- at least one special case character from this set of characters: !@#$_'
}

pkg=iccr-${version}.tgz
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
mac=0
darwin=`uname | grep -i darwin`
if [ "${darwin}" = "Darwin" ]; then
    mac=1
fi

if [ ! -f $pkg ]; then
    echo "Failed to find the required ICCR archive file ($pkg) in the local directory!"
    echo "I can't continue"
    exit
fi

echo
echo "This script will install the $what into $iccrDir"
echo
if [ ! -d $iccrDir ]; then
    echo "That directory does not exist, it will have to be created"
    echo
    echo "Do you need 'sudo' permission to create the new directory $iccrDir if needed? [Y/n]"

    needPerm=Y
    read needPerm

    if [ -z "${needPerm}" ]; then
	needPerm=Y
    fi
fi

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

# echo "hasJava $hasJava, hasRightJava $hasRightJava"

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
       echo "You will need 'sudo' permission to do this"
       echo
       if [ "${doesApt}" = "1" ]; then
           if [ "${doWhat}" = "install" ]; then
               echo "sudo apt-get -y update"
               sudo apt-get -y update
               echo
               echo "sudo apt-get -y install default-jre"
               sudo apt-get -y install default-jre
           else
               echo "sudo apt-get -y update"
               sudo apt-get -y update
               echo
               echo "sudo apt-get -y install default-jre"
               sudo apt-get -y install default-jre
           fi
       else
           if [ "${doesYum}" = "1" ]; then
               echo
               echo "Sorry, you'll have to download the right java version before installing $what"
               exit
               if [ "${doWhat}" = "install" ]; then
                   echo "sudo yum install openjdk"
                   sudo yum install openjdk
               else
                   echo "sudo yum install openjdk"
                   sudo yum install openjdk
               fi
           fi
       fi
       echo
       echo "Ok, java $doWhat attempt finished"
       echo
   fi
fi

if [ ! -d $dir ]; then
    echo "Creating $dir"
    if [ "${needPerm}" = "Y" ]; then
        sudo mkdir $dir
        sudo chmod a+x $dir
        sudo chmod a+r $dir
        sudo chmod a+w $dir
    else
        mkdir $dir
	rval=$?
	if [ "${rval}" = "1" ]; then
            echo "It appears that the 'mkdir $dir' command failed!"
	    echo "Your account is not authorized, you may need 'sudo' permission to create that directory."
	    exit
	fi

    fi
fi

if [ ! -d $iccrDir ]; then
    echo "Creating $iccrDir"
    if [ "${needPerm}" = "Y" ]; then
        sudo mkdir $iccrDir
        sudo chmod a+x $iccrDir
        sudo chmod a+r $iccrDir
        sudo chmod a+w $iccrDir
    else
        mkdir $iccrDir
	rval=$?
	if [ "${rval}" = "1" ]; then
            echo "It appears that the 'mkdir $iccrDir' command failed!"
	    echo "Your account is not authorized, you may need 'sudo' permission to create that directory."
	    exit
	fi

    fi
fi

if [ ! -d $iotaDir ]; then
    echo "Creating $iotaDir"
    if [ "${needPerm}" = "Y" ]; then
        sudo mkdir $iotaDir
        sudo chmod a+x $iotaDir
        sudo chmod a+r $iotaDir
        sudo chmod a+w $iotaDir
    else
        mkdir $iotaDir
	rval=$?
	if [ "${rval}" = "1" ]; then
            echo "It appears that the 'mkdir $iotaDir' command failed!"
	    echo "Your account is not authorized, you may need 'sudo' permission to create that directory."
	    exit
	fi
    fi
fi

echo
echo "cd $dir"
cd $dir

echo
echo "tar xzvf $curDir/$pkg"
tar xzvf $curDir/$pkg

origApiKey=`grep $iccrApiKeyProp $iccrPropFile | sed -e "s/${iccrApiKeyProp}=//g"`

echo
echo "You need to set the $what API access key (password) now."
echo
printApiKeyHelp
echo
echo -n "Enter an API access key:  "
read pwd
if [ -z "${pwd}" ]; then
    echo -n "Enter an API access key:  "
    read pwd
fi

let try=0
checkApiKeyComplexity $pwd
while [ $? -eq 1 ]; do
    let try=$try+1
    if [ $try -gt 5 ]; then
	echo "Ok, we'll let you use the default API access key value: \"$origApiKey\""
        pwd=$origApiKey
    else
        printApiKeyHelp
        echo
        echo -n "Enter a stronger API access key:  "
        read pwd
        checkApiKeyComplexity $pwd
    fi
done

echo "Ok, using $pwd as the $what API access key..."
if [ "${mac}" = "1" ]; then
    sed -i '' "s/^${iccrApiKeyProp}=.*$/${iccrApiKeyProp}=${pwd}/g" $iccrPropFile
else
    sed -i "s/^${iccrApiKeyProp}=.*$/${iccrApiKeyProp}=${pwd}/g" $iccrPropFile
fi

echo
echo Done
echo




