#!/bin/bash
pcount=$#
if((pcount<1)) ; then
    echo no args;
    exit;
fi

echo
for((host=1;host<=9;host=host+1)); do
    echo ---------- node0$host ----------------
    ssh node0$host "$@"
    echo
done

echo "$0 done."
exit 0