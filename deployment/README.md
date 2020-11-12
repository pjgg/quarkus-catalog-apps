# Deployment

## Requirements

* Helm
* [helm-diff plugin](https://github.com/databus23/helm-diff)
* helmfile
    * How to install(Linux):
    ```
      wget https://github.com/roboll/helmfile/releases/download/v0.134.0/helmfile_linux_amd64 -O helmfile_linux_amd64
      chmod +x helmfile_linux_amd64
      sudo mv helmfile_linux_amd64 /usr/local/bin/helmfile
    ```
  * [oc](https://docs.openshift.com/enterprise/3.2/cli_reference/manage_cli_profiles.html#manually-configuring-cli-profiles) / kubectl Cli


## Helm

Move on to your helmfile environment, for example local and then apply your changes:
> cd /helmfiles/local
>
> helmfile -f helmfile.yaml apply/sync


### Folder Structure
```
.
├── charts
│   └── catalog-rest-api
│       ├── Chart.yaml
│       ├── templates
│       │   ├── deployment.yaml
│       │   ├── _helpers.tpl
│       │   └── service.yaml
│       └── values.yaml
├── helmfiles
│   ├── dev
│   │   └── helmfile.yaml
│   ├── local
│   │   ├── helmfile.yaml
│   │   └── values.yaml
│   └── prod
├── README.md
└── values
    ├── dev
    │   └── catalog-rest-api
    │       └── values.yaml
    └── replica-values.yaml
```
**Chart:** a collection of k8s templates and default values that applies to those templates. Doesn't talk about environment specific values.

**helmfiles:** Is a declarative spec for deploying helm charts. Here you have environment specific values. A helmfile could deploy more than one chart, 
and also DBs, brokers, secrets etc...everything that is required in order to make your service running must be defined in these helmfiles. 

**values:** these values overwrite default template values, and are environment specific.   
