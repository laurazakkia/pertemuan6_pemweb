from flask import Flask, render_template, request, redirect, url_for, session, make_response
import psycopg2
from datetime import timedelta

app = Flask(__name__)
app.secret_key = 'secret123'  
app.permanent_session_lifetime = timedelta(seconds=60)  

DB_NAME = "twitterdb"
DB_USER = "postgres"
DB_PASS = "Laura12345"
DB_HOST = "localhost"
DB_PORT = "5432"

def get_db_connection():
    try:
        return psycopg2.connect(
            dbname=DB_NAME, user=DB_USER, password=DB_PASS, host=DB_HOST, port=DB_PORT
        )
    except psycopg2.Error as e:
        print("Gagal terhubung ke database:", e)
        return None

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username')
        if username:
            session.permanent = True  
            session['username'] = username  
            print("Setting cookie:", username)
            resp = make_response(redirect(url_for('home')))
            resp.set_cookie('username', username, max_age=60)  
            return resp
    return render_template('login.html')

@app.route('/logout')
def logout():
    session.pop('username', None)  
    resp = make_response(redirect(url_for('login')))
    resp.set_cookie('username', '', expires=0)  
    return resp

@app.route('/')
def home():
    username = session.get('username') or request.cookies.get('username')
    if not username:
        return redirect(url_for('login'))

    conn = get_db_connection()
    if conn is None:
        return "Gagal terhubung ke database", 500

    try:
        with conn:
            with conn.cursor() as cur:
                cur.execute("SELECT id, name, username, bio FROM users WHERE username = %s;", (username,))
                user = cur.fetchone()
                if not user:
                    return "User tidak ditemukan", 404

                user_id = user[0]
                cur.execute("SELECT COUNT(*) FROM follows WHERE follower_id = %s;", (user_id,))
                following_count = cur.fetchone()[0]

                cur.execute("SELECT COUNT(*) FROM follows WHERE following_id = %s;", (user_id,))
                follower_count = cur.fetchone()[0]

                cur.execute("""
                    SELECT pe.name, pe.username, po.content
                    FROM users pe
                    JOIN post po ON po.id_pengguna = pe.id
                    WHERE po.id_pengguna = %s
                            LIMIT 30;
                """, (user_id,))
                posts = cur.fetchall()

        return render_template('index.html', 
                             user=user, 
                             following_count=following_count, 
                             follower_count=follower_count, 
                             posts=posts)
    except Exception as e:
        print("Error:", e)
        return "Terjadi kesalahan", 500
    finally:
        conn.close()

@app.route('/following')
def following():
    username = session.get('username') or request.cookies.get('username')
    if not username:
        return redirect(url_for('login'))

    conn = get_db_connection()
    if conn is None:
        return "Gagal terhubung ke database", 500

    with conn:
        with conn.cursor() as cur:
            cur.execute("SELECT id, name, username FROM users WHERE username = %s;", (username,))
            user = cur.fetchone()
            if not user:
                return "User tidak ditemukan", 404

            user_id = user[0]
            cur.execute("""
                SELECT users.name, users.username 
                FROM follows 
                INNER JOIN users ON follows.following_id = users.id 
                WHERE follows.follower_id = %s;
            """, (user_id,))
            following_users = cur.fetchall()

    return render_template('following.html', user=user, following_users=following_users)

if __name__ == '__main__':
    app.run(debug=True)
