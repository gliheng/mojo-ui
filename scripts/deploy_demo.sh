git checkout master
lein do clean, cljsbuild once min

git checkout gh-pages
cp -r ./resources/public/* .
git commit -am "new version"
git push

git checkout master
