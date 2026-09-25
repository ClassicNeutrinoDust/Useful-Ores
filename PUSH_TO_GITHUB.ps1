# After creating the empty GitHub repository:
# https://github.com/new?name=useful-ores

$remote = 'https://github.com/ClassicNeutrinoDust/useful-ores.git'
git remote remove origin 2>$null
git remote add origin $remote
git push -u origin main
