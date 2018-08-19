
echo "## setup..."
git config --global user.name "Stephen Colebourne (CI)"
git config --global user.email "scolebourne@joda.org"
cd target

echo "## clone..."
git clone https://${GITHUB_TOKEN}@github.com/ThreeTen/threeten.github.io.git
cd threeten.github.io
git status

echo "## copy..."
rm -rf threetenbp/
cp -R ../site threetenbp/

echo "## update..."
git add -A
git status
git commit --message "Update threetenbp from Travis: Build $TRAVIS_BUILD_NUMBER"

echo "## push..."
git push origin master

echo "## done"
