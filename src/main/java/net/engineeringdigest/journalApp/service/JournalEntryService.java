package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;   // 👈 instance

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try {
            User user = userService.findByUserName(userName);      // get the owning user
            JournalEntry saved = journalEntryRepository.save(journalEntry); // ✅ use the instance
            user.getJournalEntries().add(saved);                   // link the entry to the user
            userService.saveEntry(user);                           // persist the user update
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the entry", e);
        }
    }

    // --- 1️⃣ FIX: use journalEntryRepository, not JournalEntryRepository ---
    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);                 // ✅
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    // --- 2️⃣ FIX: use the user variable, not the User class ---
    public void deleteById(ObjectId id, String userName) {
        User user = userService.findByUserName(userName);
        user.getJournalEntries().removeIf(x -> x.getId().equals(id)); // ✅
        userService.saveEntry(user);
        journalEntryRepository.deleteById(id);
    }
}
