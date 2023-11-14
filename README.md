# Google BigQuery Storage Client for Java

Java idiomatic client for [BigQuery Storage][product-docs].

[![Maven][maven-version-image]][maven-version-link]
![Stability][stability-image]

- [Product Documentation][product-docs]
- [Client Library Documentation][javadocs]


## Quickstart

If you are using Maven with [BOM][libraries-bom], add this to your pom.xml file:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud</groupId>
      <artifactId>libraries-bom</artifactId>
      <version>26.26.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-bigquerystorage</artifactId>
  </dependency>

```

If you are using Maven without the BOM, add this to your dependencies:

<!-- {x-version-update-start:google-cloud-bigquerystorage:released} -->

```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-bigquerystorage</artifactId>
  <version>2.44.1</version>
</dependency>

```

If you are using Gradle 5.x or later, add this to your dependencies:

```Groovy
implementation platform('com.google.cloud:libraries-bom:26.27.0')

implementation 'com.google.cloud:google-cloud-bigquerystorage'
```
If you are using Gradle without BOM, add this to your dependencies:

```Groovy
implementation 'com.google.cloud:google-cloud-bigquerystorage:2.45.0'
```

If you are using SBT, add this to your dependencies:

```Scala
libraryDependencies += "com.google.cloud" % "google-cloud-bigquerystorage" % "2.45.0"
```
<!-- {x-version-update-end} -->

## Authentication

See the [Authentication][authentication] section in the base directory's README.

## Authorization

The client application making API calls must be granted [authorization scopes][auth-scopes] required for the desired BigQuery Storage APIs, and the authenticated principal must have the [IAM role(s)][predefined-iam-roles] required to access GCP resources using the BigQuery Storage API calls.

## Getting Started

### Prerequisites

You will need a [Google Cloud Platform Console][developer-console] project with the BigQuery Storage [API enabled][enable-api].
You will need to [enable billing][enable-billing] to use Google BigQuery Storage.
[Follow these instructions][create-project] to get your project set up. You will also need to set up the local development environment by
[installing the Google Cloud Command Line Interface][cloud-cli] and running the following commands in command line:
`gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.

### Installation and setup

You'll need to obtain the `google-cloud-bigquerystorage` library.  See the [Quickstart](#quickstart) section
to add `google-cloud-bigquerystorage` as a dependency in your code.

## About BigQuery Storage


[BigQuery Storage][product-docs] is an API for reading data stored in BigQuery. This API provides direct, high-throughput read
access to existing BigQuery tables, supports parallel access with automatic liquid sharding, and allows fine-grained
control over what data is returned.

See the [BigQuery Storage client library docs][javadocs] to learn how to
use this BigQuery Storage Client Library.





## Samples

Samples are in the [`samples/`](https://github.com/googleapis/java-bigquerystorage/tree/main/samples) directory.

| Sample                      | Source Code                       | Try it |
| --------------------------- | --------------------------------- | ------ |
| Parallel Write Committed Stream | [source code](https://github.com/googleapis/java-bigquerystorage/blob/main/samples/snippets/src/main/java/com/example/bigquerystorage/ParallelWriteCommittedStream.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-bigquerystorage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/bigquerystorage/ParallelWriteCommittedStream.java) |
| Storage Arrow Sample | [source code](https://github.com/googleapis/java-bigquerystorage/blob/main/samples/snippets/src/main/java/com/example/bigquerystorage/StorageArrowSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-bigquerystorage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/bigquerystorage/StorageArrowSample.java) |
| Storage Sample | [source code](https://github.com/googleapis/java-bigquerystorage/blob/main/samples/snippets/src/main/java/com/example/bigquerystorage/StorageSample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-bigquerystorage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/bigquerystorage/StorageSample.java) |
| Write Buffered Stream | [source code](https://github.com/googleapis/java-bigquerystorage/blob/main/samples/snippets/src/main/java/com/example/bigquerystorage/WriteBufferedStream.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-bigquerystorage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/bigquerystorage/WriteBufferedStream.java) |
| Write Committed Stream | [source code](https://github.com/googleapis/java-bigquerystorage/blob/main/samples/snippets/src/main/java/com/example/bigquerystorage/WriteCommittedStream.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-bigquerystorage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/bigquerystorage/WriteCommittedStream.java) |
| Write Pending Stream | [source code](https://github.com/googleapis/java-bigquerystorage/blob/main/samples/snippets/src/main/java/com/example/bigquerystorage/WritePendingStream.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-bigquerystorage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/bigquerystorage/WritePendingStream.java) |
| Write To Default Stream | [source code](https://github.com/googleapis/java-bigquerystorage/blob/main/samples/snippets/src/main/java/com/example/bigquerystorage/WriteToDefaultStream.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-bigquerystorage&page=editor&open_in_editor=samples/snippets/src/main/java/com/example/bigquerystorage/WriteToDefaultStream.java) |



## Troubleshooting

To get help, follow the instructions in the [shared Troubleshooting document][troubleshooting].

## Transport

BigQuery Storage uses gRPC for the transport layer.

## Supported Java Versions

Java 8 or above is required for using this client.

Google's Java client libraries,
[Google Cloud Client Libraries][cloudlibs]
and
[Google Cloud API Libraries][apilibs],
follow the
[Oracle Java SE support roadmap][oracle]
(see the Oracle Java SE Product Releases section).

### For new development

In general, new feature development occurs with support for the lowest Java
LTS version covered by  Oracle's Premier Support (which typically lasts 5 years
from initial General Availability). If the minimum required JVM for a given
library is changed, it is accompanied by a [semver][semver] major release.

Java 11 and (in September 2021) Java 17 are the best choices for new
development.

### Keeping production systems current

Google tests its client libraries with all current LTS versions covered by
Oracle's Extended Support (which typically lasts 8 years from initial
General Availability).

#### Legacy support

Google's client libraries support legacy versions of Java runtimes with long
term stable libraries that don't receive feature updates on a best efforts basis
as it may not be possible to backport all patches.

Google provides updates on a best efforts basis to apps that continue to use
Java 7, though apps might need to upgrade to current versions of the library
that supports their JVM.

#### Where to find specific information

The latest versions and the supported Java versions are identified on
the individual GitHub repository `github.com/GoogleAPIs/java-SERVICENAME`
and on [google-cloud-java][g-c-j].

## Versioning


This library follows [Semantic Versioning](http://semver.org/).



## Contributing


Contributions to this library are always welcome and highly encouraged.

See [CONTRIBUTING][contributing] for more information how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in
this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more
information.


## License

Apache 2.0 - See [LICENSE][license] for more information.

## CI Status

Java Version | Status
------------ | ------
Java 8 | [![Kokoro CI][kokoro-badge-image-2]][kokoro-badge-link-2]
Java 8 OSX | [![Kokoro CI][kokoro-badge-image-3]][kokoro-badge-link-3]
Java 8 Windows | [![Kokoro CI][kokoro-badge-image-4]][kokoro-badge-link-4]
Java 11 | [![Kokoro CI][kokoro-badge-image-5]][kokoro-badge-link-5]

Java is a registered trademark of Oracle and/or its affiliates.

[product-docs]: https://cloud.google.com/bigquery/docs/reference/storage/
[javadocs]: https://cloud.google.com/java/docs/reference/google-cloud-bigquerystorage/latest/history
[kokoro-badge-image-1]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java7.svg
[kokoro-badge-link-1]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java7.html
[kokoro-badge-image-2]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java8.svg
[kokoro-badge-link-2]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java8.html
[kokoro-badge-image-3]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java8-osx.svg
[kokoro-badge-link-3]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java8-osx.html
[kokoro-badge-image-4]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java8-win.svg
[kokoro-badge-link-4]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java8-win.html
[kokoro-badge-image-5]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java11.svg
[kokoro-badge-link-5]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-bigquerystorage/java11.html
[stability-image]: https://img.shields.io/badge/stability-stable-green
[maven-version-image]: https://img.shields.io/maven-central/v/com.google.cloud/google-cloud-bigquerystorage.svg
[maven-version-link]: https://central.sonatype.com/artifact/com.google.cloud/google-cloud-bigquerystorage/2.45.0
[authentication]: https://github.com/googleapis/google-cloud-java#authentication
[auth-scopes]: https://developers.google.com/identity/protocols/oauth2/scopes
[predefined-iam-roles]: https://cloud.google.com/iam/docs/understanding-roles#predefined_roles
[iam-policy]: https://cloud.google.com/iam/docs/overview#cloud-iam-policy
[developer-console]: https://console.developers.google.com/
[create-project]: https://cloud.google.com/resource-manager/docs/creating-managing-projects
[cloud-cli]: https://cloud.google.com/cli
[troubleshooting]: https://github.com/googleapis/google-cloud-java/blob/main/TROUBLESHOOTING.md
[contributing]: https://github.com/googleapis/java-bigquerystorage/blob/main/CONTRIBUTING.md
[code-of-conduct]: https://github.com/googleapis/java-bigquerystorage/blob/main/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[license]: https://github.com/googleapis/java-bigquerystorage/blob/main/LICENSE
[enable-billing]: https://cloud.google.com/apis/docs/getting-started#enabling_billing
[enable-api]: https://console.cloud.google.com/flows/enableapi?apiid=bigquerystorage.googleapis.com
[libraries-bom]: https://github.com/GoogleCloudPlatform/cloud-opensource-java/wiki/The-Google-Cloud-Platform-Libraries-BOM
[shell_img]: https://gstatic.com/cloudssh/images/open-btn.png

[semver]: https://semver.org/
[cloudlibs]: https://cloud.google.com/apis/docs/client-libraries-explained
[apilibs]: https://cloud.google.com/apis/docs/client-libraries-explained#google_api_client_libraries
[oracle]: https://www.oracle.com/java/technologies/java-se-support-roadmap.html
[g-c-j]: http://github.com/googleapis/google-cloud-java
