# Structurizr DSL

`backend/docs/structurizr/` でバックエンド構成図を管理します。

## Files

- `workspace.dsl`: レイヤ分けしたコンポーネント図の定義
- `compose.yaml`: `Structurizr` ローカルビューアを起動するための定義

## Usage

このディレクトリで次を実行します。

```bash
docker compose up
```

起動後に `http://localhost:8080` を開くと、`workspace.dsl` の内容を確認できます。

## 現在のモデリング方針

- バックエンド本体は `Entrypoint -> Handler -> Service -> Domain / Repository` のレイヤで表現する
- 上位 AWS インフラからバックエンドにリクエストが流入する経路を図に含める
- `Domain` は各コンポーネント内、またはコンポーネント間で扱うデータモデルと型の定義として扱う
- DB はバックエンド外部の要素として表示し、`PostGIS` による地理空間検索責務を明示する
- マイグレーションとインフラ構築要素は今回の図の対象外とする
