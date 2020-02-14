for i in export/*.csv; do
    basefile=$(basename $i)
    echo " sharing/${basefile}"
    head -100 $i > sharing/${basefile}
done
