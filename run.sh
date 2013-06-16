mkdir -p pluginClasses
mkdir -p Plugins
cp ../../Plugins/LucenePlugin/dist/LuceneIndexPlugin-all.jar Plugins/
cp ../../Plugins/JGroupsPlugin/dist/JgroupsPlugin-all.jar Plugins/
cp ../../SDK/dist/dicoogle-SDK.jar lib/
cp -R /Users/bastiao/Projects/devel/apps/StatisticPlugin/dist/StatisticsPlugin-all.jar Plugins/
java -jar Dicoogle.jar
