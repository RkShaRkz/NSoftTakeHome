package com.nsoft.github.domain.model

data class GitCollaboratorList(
    val collaborators: List<GitCollaborator>
): ResponseDomainData()
