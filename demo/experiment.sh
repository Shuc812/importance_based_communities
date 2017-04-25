rm -f $2.$1
echo "Results for $1 $2" >> $2.$1

echo "r=10" >> $2.$1
echo "k=2" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 2  >> $2.$1
echo "k=4" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 4  >> $2.$1
echo "k=8" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 8  >> $2.$1
echo "k=16" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 16  >> $2.$1
echo "k=32" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 32  >> $2.$1
echo "k=64" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 64  >> $2.$1
echo "k=128" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 128  >> $2.$1
echo "k=256" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 256  >> $2.$1
echo "k=512" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 10 512  >> $2.$1

echo "r=20" >> $2.$1
echo "k=2" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 2  >> $2.$1
echo "k=4" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 4  >> $2.$1
echo "k=8" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 8  >> $2.$1
echo "k=16" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 16  >> $2.$1
echo "k=32" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 32  >> $2.$1
echo "k=64" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 64  >> $2.$1
echo "k=128" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 128  >> $2.$1
echo "k=256" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 256  >> $2.$1
echo "k=512" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 20 512  >> $2.$1

echo "r=40" >> $2.$1
echo "k=2" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 2  >> $2.$1
echo "k=4" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 4  >> $2.$1
echo "k=8" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 8  >> $2.$1
echo "k=16" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 16  >> $2.$1
echo "k=32" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 32  >> $2.$1
echo "k=64" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 64  >> $2.$1
echo "k=128" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 128  >> $2.$1
echo "k=256" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 256  >> $2.$1
echo "k=512" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 40 512  >> $2.$1

echo "r=80" >> $2.$1
echo "k=2" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 2  >> $2.$1
echo "k=4" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 4  >> $2.$1
echo "k=8" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 8  >> $2.$1
echo "k=16" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 16  >> $2.$1
echo "k=32" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 32  >> $2.$1
echo "k=64" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 64  >> $2.$1
echo "k=128" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 128  >> $2.$1
echo "k=256" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 256  >> $2.$1
echo "k=512" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 80 512  >> $2.$1

echo "r=160" >> $2.$1
echo "k=2" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 2  >> $2.$1
echo "k=4" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 4  >> $2.$1
echo "k=8" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 8  >> $2.$1
echo "k=16" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 16  >> $2.$1
echo "k=32" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 32  >> $2.$1
echo "k=64" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 64  >> $2.$1
echo "k=128" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 128  >> $2.$1
echo "k=256" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 256  >> $2.$1
echo "k=512" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 160 512  >> $2.$1

echo "r=320" >> $2.$1
echo "k=2" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 2  >> $2.$1
echo "k=4" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 4  >> $2.$1
echo "k=8" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 8  >> $2.$1
echo "k=16" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 16  >> $2.$1
echo "k=32" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 32  >> $2.$1
echo "k=64" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 64  >> $2.$1
echo "k=128" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 128  >> $2.$1
echo "k=256" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 256  >> $2.$1
echo "k=512" >> $2.$1
timeout 3600s java -cp "../lib/*":"../bin" $1 $2 320 512  >> $2.$1

echo ""
echo ""
