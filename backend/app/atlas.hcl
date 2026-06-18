env "dev" {
  src = "file://db/schema"
  dev = env("ATLAS_DEV_URL")

  migration {
    dir = "file://db/migrations"
  }
}
