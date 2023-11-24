package com.coco.gitcompose.core.datamodel

enum class RepoType(val typeName: String) {
    ALL("all"), OWNER("owner"), PUBLIC("public"), PRIVATE("private"), MEMBER("member")
}