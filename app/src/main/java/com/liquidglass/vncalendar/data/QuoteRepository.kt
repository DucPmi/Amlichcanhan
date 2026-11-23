package com.liquidglass.vncalendar.data

import java.time.LocalDate
import kotlin.random.Random

data class EnglishQuote(
    val text: String,
    val author: String
)

object QuoteRepository {

    private val quotes = listOf(
        EnglishQuote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Will Durant"),
        EnglishQuote("Discipline is choosing between what you want now and what you want most.", "Abraham Lincoln"),
        EnglishQuote("Small daily improvements over time lead to stunning results.", "Robin Sharma"),
        EnglishQuote("Clarity precedes mastery.", "Robin Sharma"),
        EnglishQuote("You do not rise to the level of your goals. You fall to the level of your systems.", "James Clear"),
        EnglishQuote("How we spend our days is, of course, how we spend our lives.", "Annie Dillard"),
        EnglishQuote("The secret of getting ahead is getting started.", "Mark Twain"),
        EnglishQuote("Simplicity is the ultimate sophistication.", "Leonardo da Vinci"),
        EnglishQuote("Action cures fear; inaction creates terror.", "David J. Schwartz"),
        EnglishQuote("Focus is a matter of deciding what things you are not going to do.", "John Carmack"),
        EnglishQuote("Do the hard jobs first. The easy jobs will take care of themselves.", "Dale Carnegie"),
        EnglishQuote("Success is the sum of small efforts, repeated day in and day out.", "Robert Collier"),
        EnglishQuote("Your future is created by what you do today, not tomorrow.", "Robert Kiyosaki"),
        EnglishQuote("Waste no more time arguing about what a good person should be. Be one.", "Marcus Aurelius"),
        EnglishQuote("The impediment to action advances action. What stands in the way becomes the way.", "Marcus Aurelius"),
        EnglishQuote("Luck is what happens when preparation meets opportunity.", "Seneca"),
        EnglishQuote("We suffer more often in imagination than in reality.", "Seneca"),
        EnglishQuote("First say to yourself what you would be; and then do what you have to do.", "Epictetus"),
        EnglishQuote("Stay hungry, stay foolish.", "Steve Jobs"),
        EnglishQuote("Details matter, it's worth waiting to get it right.", "Steve Jobs"),
        EnglishQuote("Make each day your masterpiece.", "John Wooden"),
        EnglishQuote("It is not the mountain we conquer, but ourselves.", "Edmund Hillary"),
        EnglishQuote("Energy and persistence conquer all things.", "Benjamin Franklin"),
        EnglishQuote("Well done is better than well said.", "Benjamin Franklin"),
        EnglishQuote("A year from now you may wish you had started today.", "Karen Lamb"),
        EnglishQuote("Consistency compounds faster than intensity.", "Naval Ravikant"),
        EnglishQuote("Play long-term games with long-term people.", "Naval Ravikant"),
        EnglishQuote("A calm mind brings inner strength and self-confidence.", "Dalai Lama"),
        EnglishQuote("Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
        EnglishQuote("Nothing will work unless you do.", "Maya Angelou"),
        EnglishQuote("Quality is not an act, it is a habit.", "Aristotle"),
        EnglishQuote("He who has a why to live can bear almost any how.", "Friedrich Nietzsche"),
        EnglishQuote("Do not wait for extraordinary circumstances to do good action; try to use ordinary situations.", "Jean Paul Richter"),
        EnglishQuote("Great things are done by a series of small things brought together.", "Vincent van Gogh"),
        EnglishQuote("The best time to plant a tree was twenty years ago. The second best time is now.", "Chinese Proverb"),
        EnglishQuote("Never confuse motion with action.", "Ernest Hemingway"),
        EnglishQuote("Courage is grace under pressure.", "Ernest Hemingway"),
        EnglishQuote("What gets measured gets managed.", "Peter Drucker"),
        EnglishQuote("Plans are nothing; planning is everything.", "Dwight D. Eisenhower"),
        EnglishQuote("In the middle of difficulty lies opportunity.", "Albert Einstein"),
        EnglishQuote("Strive not to be a success, but rather to be of value.", "Albert Einstein"),
        EnglishQuote("Everything you want is on the other side of consistency.", "Unknown"),
        EnglishQuote("Quiet the mind, and the soul will speak.", "Ma Jaya Sati Bhagavati"),
        EnglishQuote("Fall seven times and stand up eight.", "Japanese Proverb"),
        EnglishQuote("One day or day one. You decide.", "Paulo Coelho"),
        EnglishQuote("When you want something, all the universe conspires in helping you to achieve it.", "Paulo Coelho"),
        EnglishQuote("Dream big. Start small. Act now.", "Robin Sharma"),
        EnglishQuote("The way you do anything is the way you do everything.", "Martha Beck"),
        EnglishQuote("Protect your peace and execute your plan.", "Unknown"),
        EnglishQuote("Progress, not perfection.", "Unknown"),
        EnglishQuote("Work hard in silence, let your success be your noise.", "Frank Ocean"),
        EnglishQuote("Show up every day, especially on the days you do not feel like it.", "Unknown"),
        EnglishQuote("Mastering others is strength. Mastering yourself is true power.", "Lao Tzu"),
        EnglishQuote("A journey of a thousand miles begins with a single step.", "Lao Tzu"),
        EnglishQuote("Nature does not hurry, yet everything is accomplished.", "Lao Tzu"),
        EnglishQuote("Be tolerant with others and strict with yourself.", "Marcus Aurelius"),
        EnglishQuote("If it is not right, do not do it; if it is not true, do not say it.", "Marcus Aurelius"),
        EnglishQuote("Done is better than perfect.", "Sheryl Sandberg"),
        EnglishQuote("Think deeply, act decisively.", "Unknown"),
        EnglishQuote("Momentum is built one kept promise at a time.", "Unknown"),
        EnglishQuote("Turn friction into fuel.", "Unknown"),
        EnglishQuote("Your habits are your vote for the person you want to become.", "James Clear"),
        EnglishQuote("Every morning brings a fresh ledger.", "Unknown"),
        EnglishQuote("Keep your standards high and your excuses at zero.", "Unknown"),
        EnglishQuote("Stay steady when the noise gets loud.", "Unknown")
    )

    fun quoteForDate(date: LocalDate): EnglishQuote {
        val index = Math.floorMod(date.toEpochDay(), quotes.size.toLong()).toInt()
        return quotes[index]
    }

    fun randomQuote(excluding: EnglishQuote? = null): EnglishQuote {
        val pool = if (excluding != null && quotes.size > 1) {
            quotes.filter { it != excluding }
        } else {
            quotes
        }
        return pool[Random.nextInt(pool.size)]
    }
}
