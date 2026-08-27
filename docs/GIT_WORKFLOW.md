# Git Workflow & Branching Strategy

## 1. Branch Utama

| Branch                      | Fungsi                                                                    |
|-----------------------------|---------------------------------------------------------------------------|
| `main`                      | Stable release branch.                                                    |
| `develop`                   | Integration branch untuk semua fitur.                                     |
| `feature/feature-name`      | Branch untuk mengerjakan fitur baru.                                      |
| `bugfix/issue-name`         | Branch untuk memperbaiki bug.                                             |
| `refactor/*` atau `chore/*` | Branch untuk perubahan core/infra yang bukan fitur baru (lihat bagian 3). |

**Aturan dasar:** semua branch kerja (`feature/*`, `bugfix/*`, `refactor/*`) dibuat dari `develop`,
dan di-merge kembali ke `develop` setelah selesai & di-review.

---

## 2. Kapan Terjadi "Scope Creep" (Core Berubah di Tengah Fitur)

Sering terjadi: saat mengerjakan `feature/a`, ternyata ada dependency di core yang belum siap,
sehingga branch `feature/a` jadi berisi perubahan di luar scope aslinya.

**Ini normal.** Bukan tanda workflow salah — biasanya berarti scope planning di awal belum menangkap
semua dependency. Berikut cara menanganinya.

---

## 3. Strategi Menangani Dependency Core

### A. Pisahkan prerequisite jadi branch sendiri (paling bersih)

Kalau kamu sadar butuh perubahan core **sebelum mulai coding fitur**:

1. Stop dulu kerjaan di `feature/a`.
2. Buat branch baru dari `develop`, misal `refactor/core-prep-for-a` atau `chore/core-x`.
3. Kerjakan & selesaikan perubahan core di situ, lalu merge ke `develop` duluan.
4. Branch ulang `feature/a` dari `develop` yang sudah ter-update.

```bash
git checkout develop
git pull
git checkout -b chore/core-x
# kerjakan perubahan core, commit
git push origin chore/core-x
# buat PR, review, merge ke develop

git checkout develop
git pull
git checkout -b feature/a
```

### B. Kalau sudah terlanjur campur di satu branch

Tidak apa-apa selama commit dijaga **atomic dan terpisah** (jangan gabung "fix core" + "add feature"
dalam satu commit). Sebelum merge/PR, rapikan dengan:

- `git rebase -i` — reorder & split commit
- Cherry-pick commit core ke branch terpisah, merge ke `develop` duluan
- Rebase `feature/a` di atas `develop` yang baru, sehingga PR fitur jadi bersih (hanya diff fitur)

```bash
git rebase -i develop        # reorder/split commit di feature/a
git checkout develop
git checkout -b chore/core-x
git cherry-pick <hash-commit-core>
# push, review, merge chore/core-x ke develop

git checkout feature/a
git rebase develop
```

### C. Stacked branches

Kalau core belum sempat di-merge tapi kamu tetap mau lanjut fitur A yang depend padanya:

- Buat `feature/a` bercabang dari branch core (bukan dari `develop`).
- Setelah PR core di-approve & merge ke `develop`, rebase `feature/a` ke `develop`.

```bash
git checkout chore/core-x
git checkout -b feature/a
# ... setelah chore/core-x merge ke develop ...
git checkout feature/a
git rebase develop
```

### D. Feature flag (untuk perubahan core yang besar/berisiko)

Kalau perubahan core cukup besar, merge ke `develop` di belakang feature flag supaya tidak
mengganggu branch lain, sambil fitur tetap dikembangkan paralel.

---

## 4. Tips Praktis Pindah Context Antar Branch

Pindah branch di tengah kerjaan itu **normal**, bukan sesuatu yang perlu dihindari. Beberapa cara
supaya tidak ribet:

### `git stash` — kalau kerjaan belum siap di-commit

```bash
git stash
git checkout develop
git checkout -b chore/core-x
# kerjakan, commit, merge ke develop
git checkout feature/a
git stash pop
```

### `git worktree` — untuk yang sering context-switch

Punya folder terpisah untuk branch berbeda, jalan bersamaan, tanpa saling ganggu working directory:

```bash
git worktree add ../myapp-core-fix chore/core-x
```

Tinggal buka folder lain di editor tanpa perlu stash/checkout sama sekali.

---

## 5. Prinsip Utama

- Git flow adalah soal **hasil akhir** (history & PR yang rapi, satu concern per PR), bukan soal
  proses kerja harus linear sempurna.
- Wajar kalau saat coding menyenggol hal di luar scope awal — yang penting sebelum merge, rapikan
  lagi jadi potongan-potongan logis (lewat rebase/cherry-pick), bukan memaksakan semuanya masuk satu
  PR besar campur aduk.
- Biasakan cek dependency sebelum mulai fitur baru: kalau ada yang belum ada di `develop`, itu
  prerequisite yang harus di-branch & merge duluan.