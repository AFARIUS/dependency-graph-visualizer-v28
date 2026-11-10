import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class MavenDependencyFetcher {
    private static final int HTTP_STATUS_OK = 200;
    private static final int EXPECTED_MAVEN_PARTS = 3;
    private static final int GROUP_ID_INDEX = 0;
    private static final int ARTIFACT_ID_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final String TAG_DEPENDENCIES = "dependencies";
    private static final String TAG_DEPENDENCY = "dependency";
    private static final String TAG_GROUP_ID = "groupId";
    private static final String TAG_ARTIFACT_ID = "artifactId";
    private static final String TAG_VERSION = "version";

    public static List<Dependency> fetchDirectDependencies(String packageCoords, String repoUrl) throws Exception {
        String pomUrl = getPomUrl(packageCoords, repoUrl);
        System.out.println("Скачиваем POM по адресу: " + pomUrl);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(pomUrl)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HTTP_STATUS_OK) {
            throw new RuntimeException("Не удалось скачать POM. Код ответа: " + response.statusCode() + ". Проверьте имя пакета или URL репозитория.");
        }

        return parsePomXml(response.body());
    }

    private static String getPomUrl(String packageCoords, String repoUrl) {
        String[] parts = packageCoords.split(":");
        if (parts.length != EXPECTED_MAVEN_PARTS) {
            throw new IllegalArgumentException("Имя пакета должно быть в формате group:artifact:version (например, junit:junit:4.13.2)");
        }

        String groupId = parts[GROUP_ID_INDEX].replace('.', '/');
        String artifactId = parts[ARTIFACT_ID_INDEX];
        String version = parts[VERSION_INDEX];

        String baseUrl = repoUrl.endsWith("/") ? repoUrl.substring(0, repoUrl.length() - 1) : repoUrl;

        return String.format("%s/%s/%s/%s/%s-%s.pom", baseUrl, groupId, artifactId, version, artifactId, version);
    }

    private static List<Dependency> parsePomXml(String xmlContent) throws Exception {
        List<Dependency> dependencies = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

        NodeList dependenciesList = doc.getElementsByTagName(TAG_DEPENDENCIES);

        if (dependenciesList.getLength() > 0) {
            Node dependenciesNode = dependenciesList.item(0);
            NodeList children = dependenciesNode.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(TAG_DEPENDENCY)) {
                    Element depElement = (Element) child;
                    String group = getTagValue(TAG_GROUP_ID, depElement);
                    String artifact = getTagValue(TAG_ARTIFACT_ID, depElement);
                    String version = getTagValue(TAG_VERSION, depElement);

                    if (group != null && artifact != null) {
                        dependencies.add(new Dependency(group, artifact, version));
                    }
                }
            }
        }
        return dependencies;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }
}