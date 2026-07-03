workspace "バックエンドのレイヤードアーキテクチャ" "バックエンドアプリケーションのレイヤードコンポーネント図。" {

    !identifiers hierarchical

    model {
        apiUser = person "API利用者" "バックエンドAPIを呼び出すクライアントアプリケーションの利用者。"

        awsPlatform = softwareSystem "AWSリクエストルーティング" "外部リクエストを受け取り、バックエンドへ転送する上位AWSインフラ。" {
            edge = container "AWSエッジ" "API Gateway やロードバランサなど、バックエンド手前のAWSコンポーネント。" "AWS managed services" {
                tags "External"
            }
        }

        backend = softwareSystem "バックエンドAPI" "OpenAPIで定義されたAPIを実装するバックエンドアプリケーション。" {
            api = container "アプリケーションランタイム" "レイヤードアーキテクチャでリクエストを処理する実行単位。" "Go, AWS Lambda" {
                entrypoint = component "Entrypoint層" "Lambdaハンドラや起動時の初期化を担う。AWSインフラからのリクエストを受け取り、処理を開始する。" "internal/entrypoint" {
                    tags "Layer", "Entrypoint"
                }

                handler = component "Handler層" "HTTPイベントをアプリケーション入力へ変換し、レスポンスを組み立てる。" "internal/handler" {
                    tags "Layer", "Handler"
                }

                service = component "Service層" "OpenAPIで定義された各APIのユースケースを実行する。" "internal/service" {
                    tags "Layer", "Service"
                }

                domain = component "Domain層" "各コンポーネント内、またはコンポーネント間で扱うデータモデルと型を定義する。" "internal/domain" {
                    tags "Layer", "Domain"
                }

                repository = component "Repository層" "Service層に対して永続化処理と地理空間データアクセスを提供する。" "internal/repository" {
                    tags "Layer", "Repository"
                }
            }
        }

        database = softwareSystem "PostgreSQL with PostGIS" "バックエンド境界の外側にあるDB。データ保存と地理空間検索の責務を持つ。" "Amazon RDS for PostgreSQL + PostGIS" {
            tags "External", "Database"
        }

        apiUser -> awsPlatform.edge "APIを呼び出す" "HTTPS"
        awsPlatform.edge -> backend.api.entrypoint "バックエンドを起動する" "Lambda event"
        backend.api.entrypoint -> backend.api.handler "リクエスト処理を委譲する"
        backend.api.handler -> backend.api.service "ユースケース実行を依頼する"
        backend.api.handler -> backend.api.domain "入出力で扱うモデルを利用する"
        backend.api.service -> backend.api.domain "処理で扱うモデルを利用する"
        backend.api.service -> backend.api.repository "データアクセスを依頼する"
        backend.api.repository -> backend.api.domain "永続化・取得対象のモデルを利用する"
        backend.api.repository -> database "保存・取得・地理空間検索を行う" "SQL/PostGIS"
    }

    views {
        systemContext backend "backend-system-context" {
            include apiUser
            include awsPlatform
            include backend
            include database
            autoLayout lr
        }

        container backend "backend-containers" {
            include apiUser
            include awsPlatform
            include *
            autoLayout lr
        }

        component backend.api "backend-layered-components" {
            include apiUser
            include awsPlatform
            include database
            include *
            autoLayout lr
        }

        styles {
            element "Person" {
                background "#0b5d7a"
                color "#ffffff"
                shape Person
            }

            element "Software System" {
                background "#11698e"
                color "#ffffff"
            }

            element "Container" {
                background "#18a999"
                color "#ffffff"
            }

            element "Layer" {
                background "#f4a261"
                color "#1f2933"
            }

            element "External" {
                background "#dce6f2"
                color "#1f2933"
                border dashed
            }

            element "Database" {
                shape Cylinder
                background "#9cc5a1"
                color "#1f2933"
            }

            element "Entrypoint" {
                shape RoundedBox
            }

            element "Handler" {
                shape RoundedBox
            }

            element "Service" {
                shape RoundedBox
            }

            element "Domain" {
                shape Hexagon
            }

            element "Repository" {
                shape Box
            }
        }
    }
}
