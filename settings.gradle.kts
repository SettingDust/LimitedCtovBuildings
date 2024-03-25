apply(
    from = "https://github.com/SettingDust/FabricKotlinTemplate/raw/main/common.settings.gradle.kts"
)

val minecraft = settings.extra["minecraft"]
val kotlin = settings.extra["kotlin"]

dependencyResolutionManagement.versionCatalogs.named("catalog") {
    // https://modrinth.com/mod/patched/versions
    library("patched", "maven.modrinth", "patched").version("3.2.3+$minecraft-fabric")
    // https://modrinth.com/mod/lithostitched/versions
    library("lithostitched", "maven.modrinth", "lithostitched").version("1.1.5-fabric,$minecraft")
    // https://modrinth.com/mod/ct-overhaul-village/versions
    library("ctov", "maven.modrinth", "ct-overhaul-village").version("3.4.2-fabric")
    // https://github.com/Bawnorton/MixinSquared
    library("mixinsquared", "com.github.bawnorton.mixinsquared", "mixinsquared-fabric")
        .version("0.1.2-beta.5")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    // https://github.com/DanySK/gradle-pre-commit-git-hooks
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.4"
}

gitHooks {
    preCommit {
        from {
            // git diff --cached --name-only --diff-filter=ACMR | while read -r a; do
            // echo ${'$'}(readlink -f ${"$"}a); ./gradlew spotlessApply -q
            // -PspotlessIdeHook="${'$'}(readlink -f ${"$"}a)" </dev/null; done
            """
            export JAVA_HOME="${System.getProperty("java.home")}"
            ./gradlew spotlessApply spotlessCheck
            """
                .trimIndent()
        }
    }
    commitMsg { conventionalCommits { defaultTypes() } }
    hook("post-commit") {
        from {
            """
            files="${'$'}(git show --pretty= --name-only | tr '\n' ' ')"
            git add ${'$'}files
            git -c core.hooksPath= commit --amend -C HEAD
            """
                .trimIndent()
        }
    }
    createHooks(true)
}

val name: String by settings

rootProject.name = name

include("mod")

include("quilt")

include("forge")
