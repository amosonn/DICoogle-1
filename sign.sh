#keytool -genkey -keystore dicoogle.keys -alias https://bioinformatics.ua.pt -validity 1000
jarsigner -keystore dicoogle.keys -storepass dicooglepacs Dicoogle.jar https://bioinformatics.ua.pt


for file in lib/*; do
	echo "$file"
	jarsigner -keystore dicoogle.keys -storepass dicooglepacs $file https://bioinformatics.ua.pt
done
