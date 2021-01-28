package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"time"

	"github.com/go-chi/chi"
	"github.com/go-chi/chi/middleware"
	_ "github.com/go-sql-driver/mysql"
	"github.com/jinzhu/gorm"
)

type ScoreInfo struct {
	Name      string `json:"name" gorm:"column:name"`
	Score     int    `json:"score" gorm:"column:score"`
	CreatedAt string `json:"created_at" gorm:"column:created_at"`
}

func main() {
	router := chi.NewRouter()

	router.Use(middleware.Logger)

	router.Post("/scores", func(rw http.ResponseWriter, r *http.Request) {
		buf, err := ioutil.ReadAll(r.Body)
		if err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusInternalServerError)
			return
		}

		scoreInfo := ScoreInfo{}
		if err := json.Unmarshal(buf, &scoreInfo); err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusBadRequest)
			return
		}

		db, err := new_db()
		if err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusInternalServerError)
			return
		}
		defer db.Close()

		if err := insert_score(db, scoreInfo); err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusInternalServerError)
			return
		}

		rw.WriteHeader(http.StatusAccepted)
	})

	router.Get("/scores", func(rw http.ResponseWriter, r *http.Request) {
		db, err := new_db()
		if err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusInternalServerError)
			return
		}
		defer db.Close()

		scoreInfos, err := select_score(db)
		if err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusInternalServerError)
			return
		}

		b, err := json.Marshal(scoreInfos)
		if err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusInternalServerError)
			return
		}

		if _, err := rw.Write(b); err != nil {
			log.Println(err)
			rw.WriteHeader(http.StatusInternalServerError)
			return
		}

		rw.WriteHeader(http.StatusAccepted)
	})

	if err := http.ListenAndServe(":8000", router); err != nil {
		log.Fatal(err)
	}
}

func select_score(db *gorm.DB) ([]ScoreInfo, error) {
	scores := []ScoreInfo{}
	if err := db.Table("scores").Find(&scores).Error; err != nil {
		return nil, err
	}

	return scores, nil
}

func insert_score(db *gorm.DB, scoreInfo ScoreInfo) error {
	scoreInfo.CreatedAt = TimeToString(time.Now())

	if err := db.Table("scores").Create(&scoreInfo).Error; err != nil {
		return err
	}

	return nil
}

func new_db() (*gorm.DB, error) {
	DBMS := "mysql"
	DBNAME := "score_db"
	USER := os.Getenv("DB_USER")
	PASS := os.Getenv("DB_PASS")
	HOST := os.Getenv("DB_HOST")
	PORT := "3306"

	PROTOCOL := fmt.Sprintf("tcp(%s:%s)", HOST, PORT)

	CONNECT := USER + ":" + PASS + "@" + PROTOCOL + "/" + DBNAME + "?charset=utf8&parseTime=true&loc=Asia%2FTokyo"

	return gorm.Open(DBMS, CONNECT)
}

func TimeToString(t time.Time) string {
	return t.Format("2006-01-02 15:04:05")
}
