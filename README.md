# java-ws
eclipse workspace 
please remote use git pull to update local git repo after modify this readme

steps to add this workspace including one project to github
1. create repo in github and rename branch name main to master
2. add eclipse-ws to git repo with "git init"
3. add files to repo with "git add ."
4. commit files
5. connect to github with "git remote add origin  https://github.com/johnnyweng1973/java-ws.git"
6. push to github with "git pull origin master --allow-unrelated-histories and git push -u origin master". because i two bransh at beginning have no common commit, so it need to pull first with first command

steps to undo unstaged changes
1. git checkout -- filename

steps to exclude files/dirs from git repo
1. add the filename or dirname to .gitignore. put .gitignore in root dir. the name will apply to current and its subdirs. you  can put
   this file to a subdir
