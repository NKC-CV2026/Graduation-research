workspace "StopPointコンポーネント図" "StopPoint API のコンポーネント図。" {

    !identifiers hierarchical

    model {
        apiUser = person "API利用者" "バックエンドAPIを呼び出すクライアントアプリケーションの利用者。"

        awsPlatform = softwareSystem "AWSリクエストルーティング" "外部リクエストを受け取り、バックエンドへ転送する上位AWSインフラ。" {
            edge = container "AWSエッジ" "API Gateway やロードバランサなど、バックエンド手前のAWSコンポーネント。" "AWS managed services" {
                tags "External"
            }
        }

        backend = softwareSystem "バックエンドAPI" "OpenAPIで定義された StopPoint API を実装するバックエンドアプリケーション。" {
            api = container "アプリケーションランタイム" "StopPoint API をレイヤードアーキテクチャで処理する実行単位。" "Go, AWS Lambda" {
                entrypoint = component "Entrypoint層" "Lambdaハンドラや起動時の初期化を担う。AWSインフラからのリクエストを受け取り、Handlerへ処理を委譲する。" "internal/entrypoint" {
                    tags "Layer", "Entrypoint"
                }

                group "Handlers" {
                    getByIDHandler = component "Handlers.GetByID" "StopPointHandlers に属する ID検索用メソッド。停止点ID検索APIの HTTP 入出力を扱う。" "internal/handler.(*StopPointHandlers).GetByID" {
                        tags "Layer", "HandlerMethod"
                    }

                    getByRangeHandler = component "Handlers.GetByRange" "StopPointHandlers に属する 範囲検索用メソッド。停止点範囲検索APIの HTTP 入出力を扱う。" "internal/handler.(*StopPointHandlers).GetByRange" {
                        tags "Layer", "HandlerMethod"
                    }
                }

                group "Services" {
                    getByIDService = component "Services.GetByID" "StopPointService に属する ID検索用メソッド。停止点IDによる取得ユースケースを実行する。" "internal/service.(*StopPointService).GetByID" {
                        tags "Layer", "ServiceMethod"
                    }

                    getByRangeService = component "Services.GetByRange" "StopPointService に属する 範囲検索用メソッド。中心座標、範囲、limit を用いた検索ユースケースを実行する。" "internal/service.(*StopPointService).GetByRange" {
                        tags "Layer", "ServiceMethod"
                    }
                }

                stopPointModel = component "StopPointModel" "StopPoint API で共有するデータモデルと型を定義する。StopPoint、GetByRangeInput、検索条件、メタデータを含む。" "internal/model" {
                    tags "Layer", "Model"
                }

                stopPointRepository = component "StopPointRepository" "StopPoint の永続化・取得を抽象化する。FindByID と FindByRange を提供し、PostGIS を用いた実検索を隠蔽する。" "internal/repository" {
                    tags "Layer", "Repository"
                }
            }
        }

        database = softwareSystem "PostgreSQL with PostGIS" "バックエンド境界の外側にあるDB。データ保存と地理空間検索の責務を持つ。" "Amazon RDS for PostgreSQL + PostGIS" {
            tags "External", "Database"
        }

        apiUser -> awsPlatform.edge "APIを呼び出す" "HTTPS"
        awsPlatform.edge -> backend.api.entrypoint "バックエンドを起動する" "Lambda event"
        backend.api.entrypoint -> backend.api.getByIDHandler "ID検索リクエストをルーティングする"
        backend.api.entrypoint -> backend.api.getByRangeHandler "範囲検索リクエストをルーティングする"
        backend.api.getByIDHandler -> backend.api.stopPointModel "入出力モデルを利用する"
        backend.api.getByIDHandler -> backend.api.getByIDService "GetByID を呼び出す"
        backend.api.getByRangeHandler -> backend.api.stopPointModel "入出力モデルを利用する"
        backend.api.getByRangeHandler -> backend.api.getByRangeService "GetByRange を呼び出す"
        backend.api.getByIDService -> backend.api.stopPointModel "共有モデルを利用する"
        backend.api.getByIDService -> backend.api.stopPointRepository "FindByID を依頼する"
        backend.api.getByRangeService -> backend.api.stopPointModel "共有モデルと検索条件を利用する"
        backend.api.getByRangeService -> backend.api.stopPointRepository "FindByRange を依頼する"
        backend.api.stopPointRepository -> backend.api.stopPointModel "永続化・取得対象モデルを利用する"
        backend.api.stopPointRepository -> database "保存・取得・地理空間検索を行う" "SQL/PostGIS"
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

        component backend.api "stop-point-components" {
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

            element "HandlerMethod" {
                shape RoundedBox
            }

            element "Service" {
                shape RoundedBox
            }

            element "ServiceMethod" {
                shape RoundedBox
            }

            element "Model" {
                shape Hexagon
            }

            element "Repository" {
                shape Box
            }

            element "Group" {
                color "#1f2933"
            }
        }
    }
}
