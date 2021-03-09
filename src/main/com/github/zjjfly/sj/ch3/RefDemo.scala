package com.github.zjjfly.sj.ch3

import cats.effect.IO
import cats.effect.concurrent.Ref
import com.github.zjjfly.sj.ch3.RefDemo.BankAccounts.{Balance, BankAccount}

/**
 * 使用cat.effects.Ref来存储共享变量,可以避免多线程的很多问题
 *
 * @author zjjfly[https://github.com/zjjfly]
 * @since 2021/2/7
 */
object RefDemo extends App {

  final class BankAccounts(ref: Ref[IO, Map[String, BankAccount]]) {

    def alterAmount(accountNumber: String, amount: Int): IO[Option[Balance]] = {
      ref.modify { allBankAccounts =>
        val maybeBankAccount = allBankAccounts.get(accountNumber).map { bankAccount =>
          bankAccount.copy(balance = bankAccount.balance + amount)
        }
        val newBankAccounts = allBankAccounts ++ maybeBankAccount.map(m => (m.number, m))
        val maybeNewBalance = maybeBankAccount.map(_.balance)
        (newBankAccounts, maybeNewBalance)
      }
    }

    def getBalance(accountNumber: String): IO[Option[Balance]] =
      ref.get.map(_.get(accountNumber).map(_.balance))

    def addAccount(account: BankAccount): IO[Unit] =
      ref.update(_ + (account.number -> account))
  }

  object BankAccounts {

    type Balance = Int

    final case class BankAccount(number: String, balance: Balance)

  }

  val example = for {
    ref <- Ref[IO].of(Map.empty[String, BankAccount])
    bankAccounts = new BankAccounts(ref)
    _ <- bankAccounts.addAccount(BankAccount("1", 0))
    _ <- bankAccounts.alterAmount("1", 50)
    _ <- bankAccounts.alterAmount("1", -25)
    endingBalance <- bankAccounts.getBalance("1")
  } yield println(endingBalance)

  example.unsafeRunSync()
}
