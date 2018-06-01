# WikiPlag REST API

provides a REST API to access the plagiarism finder and the database 

## Build and Run locally

```bash
./sbt
>project restApi
> jetty:start
> browse
```

if you want sbt to restart the jetty server on code changes use 
```bash
> ~;jetty:stop;jetty:start
```
instead of 
```bash
> jetty:start
```
## Build Artifact
```bash
sbt "project restApi" clean assembly
```
NOTE: If the artifact is going to be deployed on an enviorment which has spark, change the following in the build.sbt: <code>libraryDependencies ++= sparkDependencies_compile</code> -> <code>libraryDependencies ++= sparkDependencies</code>
## Run
```bash
java -jar wiki_rest.jar
```

## Routes

### POST "/wikiplag/rest/analyse" 
#### Does a plagiarism analysis for the given input text and returns potential plagiarisms in json format.


Example: 

- Request Payload:
```json
{
  "text": "Daniel Stenberg, der Programmierer von cURL, begann 1997 ein Programm zu schreiben,das IRC-Teilnehmern Daten über Wechselkurse zur Verfügung stellen sollte, welche von Webseiten abgerufen werden mussten. Er hat dabei auf das schon vorhandene und sehr verbreitete Open-Source-Tool httpget aufgesetzt. Nach einer Erweiterung um andere Protokolle wurde das Programm am 20. März 1998 als cURL 4 erstmals veröffentlicht." 
}
```
- RESPONSE
```json
{
    "elapsed_time": 1650, 
    "plags": [
        {
            "id": 0, 
            "wiki_excerpts": [
                {
                    "end": 202, 
                    "end_of_plag_in_wiki": 796, 
                    "excerpt": "[...] .== Geschichte ==<span class=\"wiki_plag\">Daniel Stenberg, der Programmierer von cURL, begann 1997 ein Programm zu schreiben, das IRC-Teilnehmern Daten über Wechselkurse zur Verfügung stellen sollte, welche von Webseiten</span> abgerufen werden mu [...]", 
                    "id": 474951, 
                    "start": 0, 
                    "start_of_plag_in_wiki": 618, 
                    "title": "CURL"
                }
            ]
        }, 
        {
            "id": 1, 
            "wiki_excerpts": [
                {
                    "end": 414, 
                    "end_of_plag_in_wiki": 983, 
                    "excerpt": "[...] httpget. Nach einer <span class=\"wiki_plag\">Erweiterung um andere Protokolle wurde das Programm am 20. März 1998 als cURL 4 erstmals</span> veröffentlicht.== [...]", 
                    "id": 474951, 
                    "start": 299, 
                    "start_of_plag_in_wiki": 895, 
                    "title": "CURL"
                }
            ]
        }
    ], 
    "tagged_input_text": "<span id='0' class='input_plag'>Daniel Stenberg, der Programmierer von cURL, begann 1997 ein Programm zu schreiben,das IRC-Teilnehmern Daten über Wechselkurse zur Verfügung stellen sollte, welche von Webseiten abgerufen werden mussten</span>. Er hat dabei auf das schon vorhandene und sehr verbreitete Open-Source-Tool httpget aufgesetzt.<span id='1' class='input_plag'> Nach einer Erweiterung um andere Protokolle wurde das Programm am 20. März 1998 als cURL 4 erstmals veröffentlicht</span>."
}
```


### GET "/wikiplag/rest/documents/{id]" 
#### Returns the text of the wikipedia document with the given id as text-plain.
  
## Good reads
#### Scalatra
* [generate project template](http://scalatra.org/getting-started/first-project.html)

* [deploy standalone](http://scalatra.org/guides/2.4/deployment/standalone.html)
